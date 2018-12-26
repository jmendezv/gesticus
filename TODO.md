#TODO

######This file records improvements to Gesticus v.2.0.x that might be of general interest:

1. The form itself has no type A/B? It should, in order to facilitate computarize management.
1. __Important:__ cal afegir el camp data a seguiments_t
1. It might be convenient to consult the history of one person
1. Copy generated files to the appropriate net unit directory automagically.
1. Worker thread that checks estadas are in the right state:
   1. On start up check the following for every stada:
      1. If current state is BAIXA no cal fer res.
      1. If current state is TANCADA no cal fer res.
      1. If current state is DOCUMENTADA cal que jo la tanqui.
      1. If current state is ACABADA i ja ha passat més un mes notificar que car lliurar la documentació: certificat, memòria i full d'avaluació.
      1. If current state is INICIADA i ja ha acabat l'estada, afegir nou registre a seguiment_t amb estat ACABADA.
      1. If current state is COMUNICADA i ja ha començat, afegir un nou registre a seguiment_t amb estat INICIADA.
      1. If current state is REGISTRADA cal que jo la comuniqui!!!
   1. Every period of time check state 
1. General data management done by hand: candidats_t, admesos_t, seguiment_t
1. Data validation of data beyond null/empty: movile, date, email...
1. Add versatile find function to find estades by virtualy any field: codi_estada, nif_docent, nom_docent...


![Thumb Up](./thumb_up.jpg)