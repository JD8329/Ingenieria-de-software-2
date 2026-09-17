# AgroValle Connect

## Visión del Producto

Para los **productores agrícolas del Valle del Cauca** (Dagua, Palmira, Buga, Tuluá, Caicedonia, Jamundí), que necesitan **vender sus cosechas directamente a comercios urbanos sin intermediación excesiva**, AgroValle Connect es una **aplicación web empresarial (Java 17 / Spring Boot)** que **conecta en tiempo real la oferta agrícola con la demanda comercial de Cali y sus alrededores**, a diferencia de la cadena de comercialización tradicional, que **encarece el producto y perjudica tanto al productor como al comprador final**.

## Integrantes del equipo

- [Juan David Gonzalez] — [rol / correo]
- [Andres Felipe Lopez] — [rol / correo]
- [Nestor Javier Hernandez] — [rol / correo]
- [Steven Andres Guacheta] — [rol / correo]

## Estrategia de ramas

Usamos **Trunk-Based Development con ramas de vida corta** (`feature/*`, `docs/*`), fusionadas directamente a `main` vía Pull Request con revisión de pares. Elegimos este modelo (en lugar de GitFlow) porque el equipo es pequeño (4 integrantes), el proyecto está en una única versión en desarrollo activo, y minimiza el costo de integración manteniendo `main` siempre desplegable.

```mermaid
gitGraph
   commit id: "chore: init proyecto Maven"
   branch feature/HU01-registro-agricultores
   checkout feature/HU01-registro-agricultores
   commit id: "feat(api): registro de agricultores"
   checkout main
   merge feature/HU01-registro-agricultores id: "PR #1 aprobado"
   branch feature/HU02-publicacion-lotes
   checkout feature/HU02-publicacion-lotes
   commit id: "feat(productos): publicacion de lotes"
   checkout main
   merge feature/HU02-publicacion-lotes id: "PR #2 aprobado"
   branch docs/documentacion-sprint0
   checkout docs/documentacion-sprint0
   commit id: "docs: backlog, dod, checkstyle, husky"
   checkout main
   merge docs/documentacion-sprint0 id: "PR #3 aprobado"
```

## Stack técnico

Java 17, Spring Boot, Maven, PostgreSQL.

## Calidad

- Linter: Checkstyle (`checkstyle.xml`)
- Pre-commit hooks: Husky (`.husky/pre-commit`)
- Definition of Done: [`docs/dod.md`](docs/dod.md)
- Backlog del producto: [`BACKLOG.md`](BACKLOG.md)
