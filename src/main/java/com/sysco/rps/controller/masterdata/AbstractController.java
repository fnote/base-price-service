package com.sysco.rps.controller.masterdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 4/11/20
 * Time: 12:54 PM
 */

public class AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    Pageable getPageable(Integer page, Integer pageSize) {
        Pageable pageable;
        if (page == null || pageSize == null) {
            //send everything
            pageable = Pageable.unpaged();
            logger.debug("send all since page or page size is not defined");
        } else {
            pageable = PageRequest.of(page, pageSize);
            logger.debug("setting page {} and  page size {}", page, pageSize);
        }
        return pageable;
    }
}
