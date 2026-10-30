# Modelo ER canónico en notación Chen

El diagrama usa rectángulos para entidades, rombos para relaciones y óvalos
para atributos. Representa el modelo relacional canónico de cinco tablas
documentado en [modelo-datos-der.md](modelo-datos-der.md); no describe el
esquema JPA actualmente desplegado.

```mermaid
flowchart LR
    MUNICIPIO[MUNICIPIO]
    AGRICULTOR[AGRICULTOR]
    PRODUCTO[PRODUCTO]
    COSECHA[COSECHA]
    OFERTA[OFERTA]

    UBICA{ubica}
    REGISTRA{registra}
    IDENTIFICA{identifica}
    PUBLICA{publica}

    MUNICIPIO ---|1| UBICA
    UBICA ---|N| AGRICULTOR
    AGRICULTOR ---|1| REGISTRA
    REGISTRA ---|N| COSECHA
    PRODUCTO ---|1| IDENTIFICA
    IDENTIFICA ---|N| COSECHA
    COSECHA ---|1| PUBLICA
    PUBLICA ---|N| OFERTA

    MUNICIPIO --- m_id([PK id])
    MUNICIPIO --- m_nombre([nombre, único])
    AGRICULTOR --- a_id([PK id])
    AGRICULTOR --- a_nombre([nombre])
    AGRICULTOR --- a_correo([correo, único])
    AGRICULTOR --- a_municipio([FK municipio_id])
    PRODUCTO --- p_id([PK id])
    PRODUCTO --- p_nombre([nombre, único])
    PRODUCTO --- p_categoria([categoría])
    COSECHA --- c_id([PK id])
    COSECHA --- c_agricultor([FK agricultor_id])
    COSECHA --- c_producto([FK producto_id])
    COSECHA --- c_cantidad([cantidad_kg])
    COSECHA --- c_fecha([fecha_cosecha])
    OFERTA --- o_id([PK id])
    OFERTA --- o_cosecha([FK cosecha_id])
    OFERTA --- o_precio([precio_kg])
    OFERTA --- o_disponible([cantidad_disponible])
    OFERTA --- o_estado([estado])
    OFERTA --- o_fecha([fecha_publicacion])

    classDef entity fill:#e8f2e8,stroke:#246b45,stroke-width:2px
    classDef relationship fill:#fff3d4,stroke:#986c00,stroke-width:2px
    classDef attribute fill:#f4f4f4,stroke:#666
    class MUNICIPIO,AGRICULTOR,PRODUCTO,COSECHA,OFERTA entity
    class UBICA,REGISTRA,IDENTIFICA,PUBLICA relationship
    class m_id,m_nombre,a_id,a_nombre,a_correo,a_municipio,p_id,p_nombre,p_categoria,c_id,c_agricultor,c_producto,c_cantidad,c_fecha,o_id,o_cosecha,o_precio,o_disponible,o_estado,o_fecha attribute
```

La cardinalidad 1:N de `COSECHA` a `OFERTA` sigue el modelo de referencia. El
esquema JPA actual agrupa atributos de cosecha y oferta en `LoteCosecha`; la
diferencia se explica en la
[correspondencia con JPA](modelo-datos-der.md#correspondencia-del-diseño-con-el-backend-jpa-actual).
