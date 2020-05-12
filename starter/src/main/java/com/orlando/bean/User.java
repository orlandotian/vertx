package com.orlando.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonAutoDetect
public class User {
  @JsonProperty
  private String _id;
  @JsonProperty
  private String _pwd;
  @JsonProperty
  private Date loginTime;
  @JsonProperty
  private String mobile;
  @JsonProperty
  private Player player;
  @JsonProperty
  private String playerId;
  @JsonProperty
  private String token;
}
