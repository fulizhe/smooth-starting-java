package com.fulizhe.ssj.propedit;

import java.util.Map;
import java.util.Properties;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(value = "可观测性接口(config)", tags = {"可观测性接口(config)"})
@Validated
@RestController
@RequestMapping("/inner/overrideconfig")
public class OverridePropertiesConfigObservabilityController
        extends AbstractCustomPropertiesConfigObservabilityController {

    public OverridePropertiesConfigObservabilityController(OverridePropertiesConfig config) {
        super(config);
    }

    // @ApiOperation(value = "更新配置项(overrideconfig)", notes =
    // "更新配置项(overrideconfig)")
    @PostMapping(value = "/update")
    public void update(@RequestBody Map<String, String> body) {
        super.update(body);
    }

    // @ApiOperation(value = "列出所有自定义配置项(overrideconfig)", notes =
    // "列出所有自定义配置项(overrideconfig)")
    @GetMapping(value = "/list")
    public Properties list() {
        return super.list();
    }
}
