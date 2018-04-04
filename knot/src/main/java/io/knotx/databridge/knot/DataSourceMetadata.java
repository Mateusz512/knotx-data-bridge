/*
 * Copyright (C) 2018 Knot.x Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.databridge.knot;

import com.google.common.base.Objects;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes a physical details of HTTP service endpoint the ServiceAdapter will use.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class DataSourceMetadata {

  private String name;
  private String adapterAddress;
  private String cacheKey;
  private JsonObject params;

  /**
   * Default constructor
   */
  public DataSourceMetadata() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public DataSourceMetadata(DataSourceMetadata other) {
    this.name = other.name;
    this.adapterAddress = other.adapterAddress;
    this.cacheKey = other.cacheKey;
    this.params = other.params.copy();
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public DataSourceMetadata(JsonObject json) {
    init();
    DataSourceMetadataConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    DataSourceMetadataConverter.toJson(this, json);
    return json;
  }

  private void init() {

  }


  /**
   * @return a name of the service
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the service the will be used on html snippet level.
   *
   * @param name name of the service
   * @return a reference to this, so the API can be used fluently
   */
  public DataSourceMetadata setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return EB adapterAddress of the service adapter
   */
  public String getAdapterAddress() {
    return adapterAddress;
  }

  /**
   * Sets the EB adapterAddress of the service adapter
   *
   * @param adapterAddress EB adapterAddress of the service adapter
   * @return a reference to this, so the API can be used fluently
   */
  public DataSourceMetadata setAdapterAddress(String adapterAddress) {
    this.adapterAddress = adapterAddress;
    return this;
  }

  /**
   * @return the cache key value
   */
  public String getCacheKey() {
    return cacheKey;
  }

  /**
   * Set the cache key
   *
   * @param cacheKey cache key
   * @return a reference to this, so the API can be used fluently
   */
  public DataSourceMetadata setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
    return this;
  }

  /**
   * @return a JsonObject with service parameters
   */
  public JsonObject getParams() {
    return params;
  }

  /**
   * Set the service parameters to be consumed by the adapter.
   *
   * @param params JsonObject with the additional parameters
   * @return a reference to this, so the API can be used fluently
   */
  public DataSourceMetadata setParams(JsonObject params) {
    this.params = params;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataSourceMetadata that = (DataSourceMetadata) o;
    return Objects.equal(name, that.name) &&
        Objects.equal(adapterAddress, that.adapterAddress) &&
        Objects.equal(cacheKey, that.cacheKey) &&
        Objects.equal(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, adapterAddress, cacheKey, params);
  }
}
