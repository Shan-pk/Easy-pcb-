package com.example.data.local

import com.example.data.model.ComponentSpec

object ComponentCatalog {
    val verifiedComponents: List<ComponentSpec> = listOf(
        // Microcontrollers & Processors
        ComponentSpec(
            refDes = "U1",
            name = "ESP32-WROOM-32E",
            mpn = "ESP32-WROOM-32E-N4",
            manufacturer = "Espressif Systems",
            category = "Microcontroller",
            value = "Dual-Core 240MHz, 4MB Flash",
            packageType = "Module SMD (38-pin)",
            voltageRating = "3.0V - 3.6V",
            currentRating = "500mA peak",
            unitCostUsd = 2.40f,
            datasheetUrl = "https://www.espressif.com/sites/default/files/documentation/esp32-wroom-32e_esp32-wroom-32ue_datasheet_en.pdf"
        ),
        ComponentSpec(
            refDes = "U1",
            name = "RP2040",
            mpn = "RP2040",
            manufacturer = "Raspberry Pi",
            category = "Microcontroller",
            value = "Dual Cortex-M0+ 133MHz",
            packageType = "QFN-56 (7x7mm)",
            voltageRating = "1.8V - 3.3V",
            currentRating = "100mA",
            unitCostUsd = 1.00f,
            datasheetUrl = "https://datasheets.raspberrypi.com/rp2040/rp2040-datasheet.pdf"
        ),
        ComponentSpec(
            refDes = "U1",
            name = "STM32F103C8T6",
            mpn = "STM32F103C8T6",
            manufacturer = "STMicroelectronics",
            category = "Microcontroller",
            value = "ARM Cortex-M3 72MHz, 64KB Flash",
            packageType = "LQFP-48",
            voltageRating = "2.0V - 3.6V",
            currentRating = "50mA",
            unitCostUsd = 2.15f,
            datasheetUrl = "https://www.st.com/resource/en/datasheet/stm32f103c8.pdf"
        ),
        ComponentSpec(
            refDes = "U1",
            name = "ATmega328P-AU",
            mpn = "ATMEGA328P-AU",
            manufacturer = "Microchip Technology",
            category = "Microcontroller",
            value = "8-bit AVR 20MHz, 32KB Flash",
            packageType = "TQFP-32",
            voltageRating = "1.8V - 5.5V",
            currentRating = "20mA",
            unitCostUsd = 1.85f,
            datasheetUrl = "https://ww1.microchip.com/downloads/en/DeviceDoc/Atmel-7810-Automotive-Microcontrollers-ATmega328P_Datasheet.pdf"
        ),

        // Voltage Regulators & Power Management
        ComponentSpec(
            refDes = "U2",
            name = "LM2596S-5.0",
            mpn = "LM2596S-5.0/NOPB",
            manufacturer = "Texas Instruments",
            category = "Step-Down Switching Regulator",
            value = "5.0V Buck, 3A Output",
            packageType = "TO-263-5 (D2PAK)",
            voltageRating = "Input: 7V - 40V, Output: 5V",
            currentRating = "3.0A",
            unitCostUsd = 1.60f,
            datasheetUrl = "https://www.ti.com/lit/ds/symlink/lm2596.pdf"
        ),
        ComponentSpec(
            refDes = "U3",
            name = "AMS1117-3.3",
            mpn = "AMS1117-3.3",
            manufacturer = "Advanced Monolithic Systems",
            category = "LDO Linear Regulator",
            value = "3.3V Fixed, 1A",
            packageType = "SOT-223",
            voltageRating = "Input: 4.5V - 12V, Output: 3.3V",
            currentRating = "1.0A max",
            unitCostUsd = 0.20f,
            datasheetUrl = "http://www.advanced-monolithic.com/pdf/ds1117.pdf"
        ),
        ComponentSpec(
            refDes = "U4",
            name = "TPS54302",
            mpn = "TPS54302DDCR",
            manufacturer = "Texas Instruments",
            category = "Synchronous Buck Converter",
            value = "4.5V-28V Input, 3A, 400kHz",
            packageType = "SOT-23-6",
            voltageRating = "Input: 4.5V - 28V",
            currentRating = "3.0A",
            unitCostUsd = 0.85f,
            datasheetUrl = "https://www.ti.com/lit/ds/symlink/tps54302.pdf"
        ),
        ComponentSpec(
            refDes = "U5",
            name = "TP4056 Li-Ion Charger",
            mpn = "TP4056",
            manufacturer = "NanJing Top Power",
            category = "Battery Charger IC",
            value = "1A Standalone Linear LiPo Charger",
            packageType = "SOP-8-PP",
            voltageRating = "Input: 4.5V - 5.5V, Batt: 4.2V",
            currentRating = "1.0A",
            unitCostUsd = 0.35f,
            datasheetUrl = "https://dlnmh9ip6v2uc.cloudfront.net/datasheets/Prototyping/TP4056.pdf"
        ),

        // Relays & Driver Transistors
        ComponentSpec(
            refDes = "K1",
            name = "Songle Relay 5V",
            mpn = "SRD-05VDC-SL-C",
            manufacturer = "Songle",
            category = "Electromechanical Relay",
            value = "Coil 5V DC, Contacts 10A 250VAC",
            packageType = "Through-Hole 5-Pin",
            voltageRating = "Coil: 5V DC, Switch: 250V AC",
            currentRating = "10A contact rating",
            unitCostUsd = 0.65f,
            datasheetUrl = "http://www.songle.com/en/pdf/2008411959539.pdf"
        ),
        ComponentSpec(
            refDes = "K2",
            name = "Songle Relay 12V",
            mpn = "SRD-12VDC-SL-C",
            manufacturer = "Songle",
            category = "Electromechanical Relay",
            value = "Coil 12V DC, Contacts 10A 250VAC",
            packageType = "Through-Hole 5-Pin",
            voltageRating = "Coil: 12V DC, Switch: 250V AC",
            currentRating = "10A contact rating",
            unitCostUsd = 0.65f,
            datasheetUrl = "http://www.songle.com/en/pdf/2008411959539.pdf"
        ),
        ComponentSpec(
            refDes = "Q1",
            name = "2N7002 N-Channel MOSFET",
            mpn = "2N7002",
            manufacturer = "ON Semiconductor",
            category = "MOSFET Driver",
            value = "60V, 115mA, Rds(on)=7.5Ω",
            packageType = "SOT-23",
            voltageRating = "60V Vds",
            currentRating = "115mA continuous, 800mA pulse",
            unitCostUsd = 0.08f,
            datasheetUrl = "https://www.onsemi.com/pdf/datasheet/2n7002-d.pdf"
        ),
        ComponentSpec(
            refDes = "Q2",
            name = "AO3400A N-Channel MOSFET",
            mpn = "AO3400A",
            manufacturer = "Alpha & Omega",
            category = "Power MOSFET",
            value = "30V, 5.7A, Rds(on)=33mΩ",
            packageType = "SOT-23",
            voltageRating = "30V Vds",
            currentRating = "5.7A",
            unitCostUsd = 0.12f,
            datasheetUrl = "http://www.aosmd.com/res/data_sheets/AO3400A.pdf"
        ),
        ComponentSpec(
            refDes = "U6",
            name = "PC817 Optocoupler",
            mpn = "PC817C",
            manufacturer = "Sharp Microelectronics",
            category = "Optoisolator",
            value = "5kVrms Galvanic Isolation",
            packageType = "DIP-4 / SOP-4",
            voltageRating = "5000Vrms isolation",
            currentRating = "50mA forward",
            unitCostUsd = 0.18f,
            datasheetUrl = "https://global.sharp/products/device/lineup/data/pdf/datasheet/pc817xnnsz_e.pdf"
        ),
        ComponentSpec(
            refDes = "U7",
            name = "ULN2003A Relay Driver Array",
            mpn = "ULN2003ADR",
            manufacturer = "Texas Instruments",
            category = "Darlington Transistor Array",
            value = "7-Channel 50V 500mA Driver with Clamp Diodes",
            packageType = "SOIC-16",
            voltageRating = "50V",
            currentRating = "500mA per channel",
            unitCostUsd = 0.40f,
            datasheetUrl = "https://www.ti.com/lit/ds/symlink/uln2003a.pdf"
        ),

        // Diodes & Protection
        ComponentSpec(
            refDes = "D1",
            name = "1N4007 Diode",
            mpn = "1N4007",
            manufacturer = "Vishay",
            category = "Rectifier / Flyback Diode",
            value = "1000V, 1A",
            packageType = "DO-41 / SMA (SMD)",
            voltageRating = "1000V Vrrm",
            currentRating = "1.0A If",
            unitCostUsd = 0.05f,
            datasheetUrl = "https://www.vishay.com/docs/88503/1n4001.pdf"
        ),
        ComponentSpec(
            refDes = "D2",
            name = "SS34 Schottky Diode",
            mpn = "SS34",
            manufacturer = "Vishay",
            category = "Schottky Rectifier",
            value = "40V, 3A, Vf=0.5V (Reverse Polarity Protection)",
            packageType = "DO-214AB (SMC)",
            voltageRating = "40V Vrrm",
            currentRating = "3.0A If",
            unitCostUsd = 0.15f,
            datasheetUrl = "https://www.vishay.com/docs/88746/ss32.pdf"
        ),
        ComponentSpec(
            refDes = "F1",
            name = "PTC Resettable Fuse 2A",
            mpn = "MF-MSMF200-2",
            manufacturer = "Bourns",
            category = "Overcurrent Protection",
            value = "2.0A Hold, 4.0A Trip, 16V",
            packageType = "1812 SMD",
            voltageRating = "16V max",
            currentRating = "2.0A I-hold",
            unitCostUsd = 0.30f,
            datasheetUrl = "https://www.bourns.com/docs/product-datasheets/mfmsmf.pdf"
        ),
        ComponentSpec(
            refDes = "TVS1",
            name = "SMAJ15A TVS Diode",
            mpn = "SMAJ15A",
            manufacturer = "Littelfuse",
            category = "Transient Voltage Suppressor",
            value = "15V Stand-off, 400W Peak",
            packageType = "SMA (DO-214AC)",
            voltageRating = "15V Vbr",
            currentRating = "16.4A Ipp",
            unitCostUsd = 0.22f,
            datasheetUrl = "https://www.littelfuse.com/media?resourcetype=datasheets&itemid=bf3519c6-11f8-450f-90e6-599e5a1b3be0&filename=littelfuse-tvs-diode-smaj-datasheet"
        ),

        // Connectors & Terminals
        ComponentSpec(
            refDes = "J1",
            name = "Screw Terminal Block 5.08mm 2-Pin",
            mpn = "1935161",
            manufacturer = "Phoenix Contact",
            category = "Connector",
            value = "12V Power Input Terminal 16A",
            packageType = "Through-Hole 5.08mm Pitch",
            voltageRating = "300V",
            currentRating = "16A",
            unitCostUsd = 0.45f,
            datasheetUrl = "https://www.phoenixcontact.com"
        ),
        ComponentSpec(
            refDes = "J2",
            name = "Screw Terminal Block 5.08mm 3-Pin (NO/COM/NC)",
            mpn = "1935174",
            manufacturer = "Phoenix Contact",
            category = "Connector",
            value = "Relay Output 16A 3-Pin",
            packageType = "Through-Hole 5.08mm Pitch",
            voltageRating = "300V",
            currentRating = "16A",
            unitCostUsd = 0.60f,
            datasheetUrl = "https://www.phoenixcontact.com"
        ),
        ComponentSpec(
            refDes = "J3",
            name = "USB Type-C Receptacle 16-Pin",
            mpn = "TYPE-C-31-M-12",
            manufacturer = "Korean Hroparts Elec",
            category = "Connector",
            value = "USB 2.0 Power/Data 5V 3A",
            packageType = "SMD + 4 Through-Hole Shell Tabs",
            voltageRating = "20V",
            currentRating = "3.0A",
            unitCostUsd = 0.50f,
            datasheetUrl = "https://datasheet.lcsc.com/lcsc/1811051515_Korean-Hroparts-Elec-TYPE-C-31-M-12_C165948.pdf"
        ),

        // Passives
        ComponentSpec(
            refDes = "R1",
            name = "Resistor 10kΩ 0805",
            mpn = "RC0805JR-0710KL",
            manufacturer = "Yageo",
            category = "Passive",
            value = "10kΩ, 1/8W, 5%",
            packageType = "0805 (2012 Metric)",
            voltageRating = "150V",
            currentRating = "N/A",
            unitCostUsd = 0.01f,
            datasheetUrl = "https://www.yageo.com"
        ),
        ComponentSpec(
            refDes = "C1",
            name = "Capacitor 100nF (0.1µF) 0805",
            mpn = "CC0805KRX7R9BB104",
            manufacturer = "Yageo",
            category = "Passive",
            value = "100nF, 50V, X7R (Decoupling)",
            packageType = "0805 (2012 Metric)",
            voltageRating = "50V",
            currentRating = "N/A",
            unitCostUsd = 0.02f,
            datasheetUrl = "https://www.yageo.com"
        ),
        ComponentSpec(
            refDes = "C2",
            name = "Electrolytic Capacitor 220µF 25V",
            mpn = "UVR1E221MED",
            manufacturer = "Nichicon",
            category = "Passive",
            value = "220µF, 25V (Bulk Input Filter)",
            packageType = "Radial Can 6.3x11mm",
            voltageRating = "25V",
            currentRating = "250mA ripple",
            unitCostUsd = 0.18f,
            datasheetUrl = "https://www.nichicon.co.jp"
        ),
        ComponentSpec(
            refDes = "L1",
            name = "Inductor 33µH 3A Shielded",
            mpn = "CDRH104RNP-330NC",
            manufacturer = "Sumida",
            category = "Passive",
            value = "33µH, 3.0A (Buck Inductor)",
            packageType = "10x10x4mm SMD",
            voltageRating = "N/A",
            currentRating = "3.2A Isat",
            unitCostUsd = 0.70f,
            datasheetUrl = "https://www.sumida.com"
        ),
        ComponentSpec(
            refDes = "D3",
            name = "SMD Green LED 0805",
            mpn = "LG R971-KN-1",
            manufacturer = "ams OSRAM",
            category = "Optoelectronics",
            value = "Green 570nm, 2.1V, 20mA",
            packageType = "0805",
            voltageRating = "2.1V Vf",
            currentRating = "20mA",
            unitCostUsd = 0.05f,
            datasheetUrl = "https://ams-osram.com"
        )
    )
}
