package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.project.Device;
import com.developlife.reviewtwits.type.project.Inflow;
import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-04-21
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatInfo extends BaseEntity{

    @Id
    @GeneratedValue
    private long statId;

    private String inflowUrl;

    private String productUrl;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    private Device device;
}