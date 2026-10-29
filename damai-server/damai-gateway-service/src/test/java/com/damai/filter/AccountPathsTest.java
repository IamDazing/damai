package com.damai.filter;

import com.damai.property.GatewayProperty;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class AccountPathsTest {
    @Test void accountPathsRequireTokenEvenWhenConfigurationOmitsThem() {
        GatewayProperty property = new GatewayProperty();
        property.setCheckTokenPaths(new String[0]);
        RequestValidationFilter filter = new RequestValidationFilter();
        ReflectionTestUtils.setField(filter, "gatewayProperty", property);
        for (String path : new String[]{"get/id", "update", "update/password", "update/email", "update/mobile", "authentication"}) {
            assertFalse(filter.skipCheckToken("/damai/user/user/" + path), path);
        }
        assertTrue(filter.skipCheckToken("/damai/user/user/login"));
        assertTrue(filter.skipCheckToken("/damai/user/user/register"));
        assertFalse(filter.skipCheckToken("/damai/order/order/pay/check"));
        for (String path : new String[]{"list", "add", "delete"}) {
            assertFalse(filter.skipCheckToken("/damai/user/ticket/user/" + path), path);
        }
    }
}
