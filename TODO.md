# TODO

###### This file contains improvements to Gesticus v.2.0.x

1. The form itself has no type A/B? It should, in order to facilitate computarize management.
1. __Important:__ cal afegir el camp data a seguiments_t
1. Copy generated files to the appropriate net unit directory automagically.
1. Add baixa: Boolean a admesos_t default false.
1. Modify seguiment_t during common events: 
   1. Registrada - Done on insert estada
   1. Comunicada - Done on send carta...
   1. Iniciada (Auto on start up)
   1. Acabada (Auto on start up)
   1. Documentada
   1. Finalitzat
   1. Baixa (No existeix una estada com a tal que donar de baixa). Posar seguiment_t.baixa a true
1. Worker thread that checks estadas are in the right state -> generate report, dayly report?
   1. On start up check the following for every estada (email automàtics):
      1. If current state is BAIXA no cal fer res.
      1. If current state is TANCADA no cal fer res.
      1. If current state is DOCUMENTADA, warning cal que jo la tanqui.
      1. If current state is ACABADA i ja ha passat més un mes notificar que car lliurar la documentació: certificat, memòria i full d'avaluació.
      1. If current state is INICIADA i ja ha acabat l'estada, afegir nou registre a seguiment_t amb estat ACABADA.
      1. If current state is COMUNICADA i ja ha començat, afegir un nou registre a seguiment_t amb estat INICIADA.
      1. If current state is REGISTRADA, WARNING cal que jo la comuniqui!!!
1. Data validation beyond null/empty: movile, date, email...
1. Add versatile find function to find estades by virtualy any field: codi_estada, nif_docent, nom_docent...
1. Notifications via email
1. Graphs/Reports
1. General data management done by hand: candidats_t, admesos_t, seguiment_t

![Thumb Up](./thumb_up.jpg)