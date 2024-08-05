package com.test.demo.meta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "code_test")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeTest {

    @Id
    @Column(name = "code_id")
    private String codeId;

    @Column(name = "code_title")
    private String codeTitle;

    @Column(name = "code_ans")
    private String codeAns;
}
