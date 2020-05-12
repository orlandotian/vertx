package com.orlando.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonAutoDetect
public class Player {
  @JsonProperty
  private String _id;
  @JsonProperty
  private String nick;
  @JsonProperty
  private Integer age = 8;
  @JsonProperty
  private Integer sex = 0;
  @JsonProperty
  private Integer attack = 0;
  @JsonProperty
  private Integer bili = 0;
  @JsonProperty
  private Integer gengu = 0;
  @JsonProperty
  private Integer shenfa = 0;
  @JsonProperty
  private Integer wuxing = 0;
  @JsonProperty
  private Integer defense = 0;
  @JsonProperty
  private Integer dodge = 0;
  @JsonProperty
  private Integer experience = 0;
  @JsonProperty
  private Integer hit = 0;
  @JsonProperty
  private Integer speed = 0;
  @JsonProperty
  private Integer hp = 0;
  @JsonProperty
  private Integer mp = 0;


  public Player() {
    _id = UUID.randomUUID().toString();
  }
}
