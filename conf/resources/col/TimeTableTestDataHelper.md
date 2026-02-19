# COL_page3.json - Multi-Timetable Test Scenarios

**Test date (roleDate):** 01/01/2026 (`20260101`)  
**Test role (roles):** DEP (Departure, traffic type R)

| Ref      | Office Name                                           | Timetables | Roles                                                                 | Season Dates                              | Match? |
|----------|-------------------------------------------------------|------------|-----------------------------------------------------------------------|-------------------------------------------|--------|
| GB000T01 | Single Valid Timetable                                | 1 (array)  | DEP/R, EXP/R, TRA/R, DES/R, GUA/R                                     | 20250101-20261231                         | Yes    |
| GB000T02 | Single Invalid Timetable-Past                         | 1 (array)  | DEP/R, INC/R, ENT/R, EXT/R, REC/N/A                                   | 20240101-20251231                         | No     |
| GB000T03 | Single Invalid Timetable-Future                       | 1 (array)  | DEP/R, EIN/R, EXC/R, REG/R, ENQ/N/A                                   | 20260201-20271231                         | No     |
| GB000T04 | single Invalid Timetable - No role                    | 1 (array)  | EXP/R, EXT/R, SCO/N/A, ENQ/N/A, IPR/N/A<br>(no DEP)                  | 20250101-20261231                         | No     |
| GB000T05 | Multiple Timetables - Both valid                      | 2 (array)  | tt[0]: DEP/R, EXP/R, TRA/R, INC/R, GUA/R<br>tt[1]: DEP/R, DES/R, ENT/R, EXT/R, REC/R | 20250101-20260331<br>20260401-20261231 | Yes    |
| GB000T06 | Multiple Timetables - One valid/One invalid - Past    | 2 (array)  | tt[0]: DEP/R, EXP/R, EIN/R, PLA/R, ENQ/N/A<br>tt[1]: DEP/R, TRA/R, DES/R, GUA/R, IPR/N/A | Valid: 20250101-20261231<br>Past: 20240101-20251231 | Yes |
| GB000T07 | Multiple Timetables - One valid/One invalid - Future  | 2 (array)  | tt[0]: DEP/R, INC/R, ENT/R, EXC/R, RFC/R<br>tt[1]: DEP/R, EXP/R, EXT/R, REG/R, REC/R | Valid: 20250101-20261231<br>Future: 20260201-20271231 | Yes |
| GB000T08 | Multiple timetables - one valid/one invalid - No role | 2 (array)  | tt[0]: DEP/R, TRA/R, GUA/R, EIN/R, DES/R<br>tt[1]: SCO/N/A, ENQ/N/A, PLA/N/A, RFC/N/A, REG/N/A<br>(no DEP) | Both: 20250101-20261231 | Yes |
| GB000T09 | Multiple timetables - Both invalid - Past and Future  | 2 (array)  | tt[0]: DEP/R, EXP/R, INC/R, ENT/R, PLA/R<br>tt[1]: DEP/R, TRA/R, EXT/R, EXC/R, REC/R | Past: 20240101-20251231<br>Future: 20260201-20271231 | No |
| GB000T10 | Multiple timetable - Both invalid - No roles          | 2 (array)  | tt[0]: EXP/R, INC/R, SCO/N/A, ENQ/N/A, PLA/N/A<br>tt[1]: TRA/R, EXT/R, RFC/N/A, IPR/N/A, DIS/N/A<br>(no DEP) | Both: 20250101-20261231 | No |