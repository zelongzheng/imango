package org.imango.spring.constant;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class IMangoOptions  {

    private String uri;

    private String database;

}
