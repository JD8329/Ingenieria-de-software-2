# Definition of Done (DoD) — AgroValle Connect

Una Historia de Usuario o tarea técnica se considera **Terminada (Done)**
únicamente cuando cumple TODOS los siguientes criterios:

## Código

- [ ] El código compila sin errores (`./mvnw clean install`)
- [ ] El código sigue el patrón de arquitectura MVC del proyecto
      (models / repositories / services / controllers)
- [ ] Se aplicaron principios SOLID y Clean Code
- [ ] Cero advertencias críticas en Checkstyle (`checkstyle.xml`)

## Pruebas

- [ ] Todas las pruebas unitarias pasan (`mvn test`)
- [ ] La cobertura de código es de al menos 60% (verificado con JaCoCo)
- [ ] Los criterios de aceptación Given-When-Then de la HU fueron
      verificados manualmente o con pruebas automatizadas

## Control de Versiones

- [ ] El trabajo se realizó en una rama `feature/HU-XX-descripcion`,
      nunca directo sobre `main`
- [ ] Los commits siguen el estándar de Conventional Commits
      (`feat:`, `fix:`, `docs:`, `test:`, `chore:`)
- [ ] La rama fue subida a GitHub (`git push origin feature/...`)

## Revisión de Código (Code Review)

- [ ] Se abrió un Pull Request hacia `main` con título y descripción claros
- [ ] Al menos un compañero de equipo revisó el PR en la pestaña
      "Files changed"
- [ ] El PR fue formalmente **aprobado** (botón "Approve"), no solo comentado
- [ ] No quedan comentarios sin resolver en el PR

## Integración Continua

- [ ] El pipeline de GitHub Actions (`.github/workflows/ci.yml`) se
      ejecutó y pasó en verde antes del merge
- [ ] Tras el merge, la rama remota fue eliminada para mantener el
      repositorio limpio

## Documentación

- [ ] Si la HU introduce un nuevo endpoint, quedó documentado (comentario
      o README de módulo)
- [ ] El `BACKLOG.md` refleja el estado actualizado de la Historia
      (ej. marcada como completada)

---

## Firmas del Equipo

Al firmar, cada integrante confirma que comprende y se compromete a
cumplir este contrato de calidad durante todo el proyecto.

| Nombre completo         | Usuario de GitHub  | Firma / Fecha         |
|--------------------------|--------------------|----------------------|
| Juan David Gonzalez      | JD8329             | David/14/09/2026     |
| Steven Andres Guacheta   | Steven-Velez       | Steven/14/09/26      |
| Andres Felipe Lopez      | Felipe-Guinnxh     | Felipe/14/09/2026    |
| Nestor Javier Hernandez  | Dante-col25        | Javier/14/09/2026    |