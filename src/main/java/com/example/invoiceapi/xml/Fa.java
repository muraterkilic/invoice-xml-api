package com.example.invoiceapi.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Fa {

    @XmlElement(name = "P_1", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private String P1;

    @XmlElement(name = "P_2", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private String P2;
}
