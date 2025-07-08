package com.example.invoiceapi.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "Faktura", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Faktura {

    @XmlElement(name = "Podmiot1", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private Podmiot1 podmiot1;

    @XmlElement(name = "Fa", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private Fa fa;
}
