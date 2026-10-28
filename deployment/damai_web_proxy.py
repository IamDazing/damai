#!/usr/bin/env python3
"""Local-only reverse proxy that signs browser requests for the Damai gateway."""
import base64
import http.client
import json
import os
from http.server import BaseHTTPRequestHandler, HTTPServer
from socketserver import ThreadingMixIn
from urllib.parse import urlsplit

from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding

ALLOWED = frozenset('''
/damai/basedata/area/current
/damai/basedata/area/getById
/damai/basedata/area/hot
/damai/basedata/area/selectCityData
/damai/order/order/cancel
/damai/order/order/get
/damai/order/order/get/cache
/damai/order/order/pay
/damai/order/order/pay/check
/damai/order/order/select/list
/damai/program/program/category/selectByParentProgramCategoryId
/damai/program/program/category/selectByType
/damai/program/program/detail
/damai/program/program/home/list
/damai/program/program/order/create/v1
/damai/program/program/order/create/v2
/damai/program/program/order/create/v3
/damai/program/program/order/create/v4
/damai/program/program/page
/damai/program/program/recommend/list
/damai/program/program/search
/damai/program/seat/relate/info
/damai/user/ticket/user/add
/damai/user/ticket/user/delete
/damai/user/ticket/user/list
/damai/user/user/authentication
/damai/user/user/captcha/check/need
/damai/user/user/captcha/get
/damai/user/user/captcha/verify
/damai/user/user/get/id
/damai/user/user/login
/damai/user/user/logout
/damai/user/user/register
/damai/user/user/update
/damai/user/user/update/email
/damai/user/user/update/mobile
/damai/user/user/update/password
'''.split())
MAX_BODY = 1048576
with open(os.environ['DAMAI_SIGNING_KEY_FILE'], 'rb') as f:
    PRIVATE_KEY = serialization.load_pem_private_key(f.read(), password=None)

class ThreadingHTTPServer(ThreadingMixIn, HTTPServer):
    daemon_threads = True

class Handler(BaseHTTPRequestHandler):
    protocol_version = 'HTTP/1.1'

    def log_message(self, fmt, *args):
        pass

    def do_GET(self):
        self.send_error(405)

    def do_POST(self):
        self.forward()

    def forward(self):
        parsed = urlsplit(self.path)
        if parsed.path not in ALLOWED or parsed.scheme or parsed.netloc:
            self.send_error(404)
            return
        if self.headers.get('Transfer-Encoding'):
            self.send_error(400)
            return
        try:
            length = int(self.headers.get('Content-Length', '0'))
        except ValueError:
            self.send_error(400)
            return
        if length < 0 or length > MAX_BODY:
            self.send_error(413)
            return
        if self.headers.get('Sec-Fetch-Site', '').lower() == 'cross-site':
            self.send_error(403)
            return
        origin = self.headers.get('Origin')
        if origin and urlsplit(origin).netloc.lower() != self.headers.get('Host', '').lower():
            self.send_error(403)
            return
        body = self.rfile.read(length) if length else b'{}'
        if body:
            if length and self.headers.get_content_type() != 'application/json':
                self.send_error(415)
                return
            try:
                payload = json.loads(body.decode('utf-8'))
                if not isinstance(payload, dict):
                    raise ValueError('expected object')
                business = json.dumps(payload, ensure_ascii=False, separators=(',', ':'))
                content = ('businessBody=' + business + '&code=0001').encode('utf-8')
                signature = base64.b64encode(PRIVATE_KEY.sign(content, padding.PKCS1v15(), hashes.SHA256())).decode('ascii')
                body = json.dumps({'code':'0001','businessBody':business,'sign':signature}, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
            except (UnicodeError, ValueError):
                self.send_error(400)
                return
        headers = {'Host':'127.0.0.1:6085', 'no_verify':'false', 'Content-Length':str(len(body)), 'Content-Type':'application/json'}
        for key in ('Content-Type', 'Accept', 'token', 'X-Requested-With', 'User-Agent'):
            if self.headers.get(key):
                headers[key] = self.headers[key]
        conn = http.client.HTTPConnection('127.0.0.1', 6085, timeout=60)
        try:
            conn.request(self.command, self.path, body=body, headers=headers)
            response = conn.getresponse()
            self.send_response(response.status)
            for key, value in response.getheaders():
                if key.lower() not in ('connection','transfer-encoding','content-length','keep-alive','proxy-authenticate','proxy-authorization','te','trailer','upgrade'):
                    self.send_header(key, value)
            self.send_header('Connection', 'close')
            self.end_headers()
            self.close_connection = True
            while True:
                chunk = response.read(65536)
                if not chunk:
                    break
                self.wfile.write(chunk)
        except (OSError, http.client.HTTPException):
            if not self.wfile.closed:
                self.close_connection = True
        finally:
            conn.close()

if __name__ == '__main__':
    ThreadingHTTPServer(('127.0.0.1', 6090), Handler).serve_forever()
