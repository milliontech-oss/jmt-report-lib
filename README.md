Report library for export to Excel and PDF
===============================================

# End of life announcement
Thise project will be retired and reached end of life by **Dec 2025**.

This project would remain "as-is" and no further changes are anticpated.

## Runtime dependencies

Require at least Java 8

## Project timeline

### v5.1
- Release date: 2022
- Major feature:
    - itext 7.2 package rename (com.itextpdf.layout.property -> com.itextpdf.layout.properties)
    - poi 5.2+
    - various dependencies upgrade

### v5.0
- Release date: 2021
- Major feature:
    - upgrade itext version from 5 to 7


### v4.2
- Release date: 2020
- Major feature:
    - rework the `extractClass` and `extractHonz` behavior
      - Refer `ExtractClassExtractHonzTest` for implementation details and limitation
      - Major use cases: dynamic columns used in Equip Attribute and ECM trend report (last 12 months rolling summary)
    - Add `msgKeyPropMap` in `ParameterData` to prevent msgKey collision with the extract criteria


### v4.1
- Release date: 2020
- Major feature:
    - add embedded PDF font support


### v4.0
- Release date: 2019
- Major feature:
    - java 8 support (java.time.instant)
        - java.time.LocalDate (v4.0.2)
    - remove all legacy features in v1.x

