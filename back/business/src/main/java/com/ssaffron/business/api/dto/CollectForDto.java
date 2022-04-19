package com.ssaffron.business.api.dto;


import com.ssaffron.business.api.entity.EmployeeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
class CollectForDto {
    private long collectForIndex;
    private String collectFortype;
    private LocalDateTime collectForRequestDate;

}
