SELECT 
    cities.ID,
    cities.DESCRIZIONE AS cityState,
    cities.CAP AS zipCode,
    cities.CODICE_FISCALE AS cfCode,
    provinces.id AS provinceId,
    provinces.DESCRIZIONE AS province,
    regioni.DESCRIZIONE AS region,
    100 AS stateId,
    'Italia' AS state
FROM
    uiltucsdb.tb_comuni AS cities
        LEFT JOIN
    uiltucsdb.tb_provincie AS provinces ON cities.ID_TB_PROVINCIE = provinces.id
        LEFT JOIN
    uiltucsdb.tb_regioni AS regioni ON cities.ID_TB_REGIONI = regioni.id 
UNION SELECT 
    nations.ID + 99000,
    nations.DESCRIZIONE AS cityState,
    NULL AS zipCode,
    nations.CODICE_CF AS cfCode,
    NULL AS provinceId,
    NULL AS province,
    NULL AS region,
    nations.ID AS stateId,
    nations.DESCRIZIONE AS state
FROM
    uiltucsdb.tb_nazioni AS nations
WHERE nations.ID <> 100 -- Escludo l'Italia
