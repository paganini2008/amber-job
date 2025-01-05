package com.doodler.amber.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: Tip
 * @Author: Fred Feng
 * @Date: 01/11/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Tip {

	private List<String> content = new ArrayList<>();
}