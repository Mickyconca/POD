## Instalación

Acceder a la carpeta tpe1-g13-parent y en el archivo run-all.sh $TP-PATH completar la segunda línea archivo con el PATH del trabajo. Luego ejecutar dicho archivo.

```bash
  ./run-all.sh
```

A continuación ingresar a la carpeta server/target/tpe1-g13-parent-server-1.0-SNAPSHOT y ejecutar run-registry.sh para correr el Registry.

```bash
  ./run-registry.sh
```

Luego correr run-server.sh en esa misma carpeta.

```bash
  ./run-server.sh
```

Por último acceder a la carpeta client/target/tpe1-g13-parent-server-1.0-SNAPSHOT y allí correr el cliente que se desee con los parámetros necesarios.


```bash
  #Cliente Admin
  ./run-admin -DserverAddress=localhost:1099 -Daction=actionName[ -DinPath=filename | -Dflight=flightCode ]

  #Cliente de Asignación de Asientos
  ./run-seatAssign -DserverAddress=localhost:1099 -Daction=actionName-Dflight=flightCode [ -Dpassenger=name | -Drow=num | -Dcol=L |-DoriginalFlight=originFlightCode ]

  #Cliente de Notificationes
  ./run-notifications -DserverAddress=localhost:1099 -Dflight=flightCode -Dpassenger=name

  #Cliente de Consulta de Mapa de Asientos
  ./run-seatMap -DserverAddress=localhost:1099 -Dflight=flightCode [-Dcategory=catName | -Drow=rowNumber ] -DoutPath=output.csv
```
