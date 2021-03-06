/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.metron.common.configuration;

import java.util.Map;
import java.util.function.BiFunction;
import org.apache.metron.stellar.common.utils.ConversionUtils;

public interface ConfigOption {

  String getKey();

  default BiFunction<String, Object, Object> transform() {
    return (s, o) -> o;
  }

  /**
   * Returns true if the map contains the key for the defined config option
   */
  default boolean containsOption(Map<String, Object> map) {
    return map.containsKey(getKey());
  }

  default void put(Map<String, Object> map, Object value) {
    map.put(getKey(), value);
  }

  default <T> T getOrDefault(Map<String, Object> map, Class<T> clazz, T defaultValue) {
    T val;
    return ((val = get(map, clazz)) == null ? defaultValue : val);
  }

  default <T> T get(Map<String, Object> map, Class<T> clazz) {
    Object obj = map.get(getKey());
    if (clazz.isInstance(obj)) {
      return clazz.cast(obj);
    } else {
      return ConversionUtils.convert(obj, clazz);
    }
  }

  default <T> T getOrDefault(Map<String, Object> map, BiFunction<String, Object, T> transform,
      Class<T> clazz, T defaultValue) {
    T val;
    return ((val = get(map, transform, clazz)) == null ? defaultValue : val);
  }

  default <T> T get(Map<String, Object> map, BiFunction<String, Object, T> transform,
      Class<T> clazz) {
    return clazz.cast(transform.apply(getKey(), map.get(getKey())));
  }

  default <T> T getTransformedOrDefault(Map<String, Object> map, Class<T> clazz, T defaultValue) {
    T val;
    return ((val = getTransformed(map, clazz)) == null ? defaultValue : val);
  }

  default <T> T getTransformed(Map<String, Object> map, Class<T> clazz) {
    return clazz.cast(transform().apply(getKey(), map.get(getKey())));
  }

}
