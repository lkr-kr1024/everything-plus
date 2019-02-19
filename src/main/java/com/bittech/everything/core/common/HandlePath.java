package com.bittech.everything.core.common;

import lombok.Data;

import java.util.Set;
//第5个录屏中断后
@Data
public class HandlePath {
    private Set<String> includePath;
    private Set<String> excludePath;
}