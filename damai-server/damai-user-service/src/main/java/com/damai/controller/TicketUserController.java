package com.damai.controller;

import com.damai.common.ApiResponse;
import com.damai.constant.Constant;
import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import com.damai.threadlocal.BaseParameterHolder;
import com.damai.dto.TicketUserDto;
import com.damai.dto.TicketUserIdDto;
import com.damai.dto.TicketUserListDto;
import com.damai.service.TicketUserService;
import com.damai.vo.TicketUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 购票人 控制层
 **/
@RestController
@RequestMapping("/ticket/user")
@Tag(name = "ticket-user", description = "购票人")
public class TicketUserController {
    
    @Autowired
    private TicketUserService ticketUserService;
    
    @Operation(summary  = "查询购票人列表")
    @PostMapping(value = "/list")
    public ApiResponse<List<TicketUserVo>> list(@Valid @RequestBody TicketUserListDto ticketUserListDto){
        requireOwnAccount(ticketUserListDto.getUserId());
        return ApiResponse.ok(ticketUserService.list(ticketUserListDto));
    }

    /** Internal-only route; the public proxy does not expose it. */
    @PostMapping(value = "/list/internal")
    public ApiResponse<List<TicketUserVo>> listInternal(@Valid @RequestBody TicketUserListDto ticketUserListDto){
        return ApiResponse.ok(ticketUserService.list(ticketUserListDto));
    }
    
    @Operation(summary  = "添加购票人")
    @PostMapping(value = "/add")
    public ApiResponse<Void> add(@Valid @RequestBody TicketUserDto ticketUserDto){
        requireOwnAccount(ticketUserDto.getUserId());
        ticketUserService.add(ticketUserDto);
        return ApiResponse.ok();
    }
    
    @Operation(summary  = "删除购票人")
    @PostMapping(value = "/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody TicketUserIdDto ticketUserIdDto){
        ticketUserService.delete(ticketUserIdDto);
        return ApiResponse.ok();
    }

    private void requireOwnAccount(Long accountId) {
        String currentUserId = BaseParameterHolder.getParameter(Constant.USER_ID);
        if (currentUserId == null || currentUserId.isBlank()) throw new DaMaiFrameException(BaseCode.LOGIN_USER_NOT_EXIST);
        if (accountId == null || !accountId.toString().equals(currentUserId)) throw new DaMaiFrameException(BaseCode.USER_EMPTY);
    }
}
