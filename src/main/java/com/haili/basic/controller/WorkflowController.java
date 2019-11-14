package com.haili.basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.haili.common.controller.CurdController;
import com.haili.basic.entity.Workflow;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author Zhoutao
 * @since 2019-11-14
*/
@RestController
@RequestMapping("/basic/workflow")
public class WorkflowController extends CurdController<Workflow> {

}
