# Trabajo obligatorio del curso Sistemas Operativos 2021

Se nos presenta la idea de implementar una agenda de vacunación, teniendo en cuenta todos los inconvenientes que esta conlleva. Al mismo tiempo se nos exige implementar distintas formas de agendarse: mediante el sitio web, aplicación móvil Coronavirus UY, línea 0800 1919 y desde un chatbot de WhatsApp. A partir de esto surgen distintos problemas de sincronización, pues no sólo el software deberá ser capaz de agendar solicitudes simultáneas de los usuarios, sino también soportar los diferentes canales de agenda disponibles. También existe la posibilidad de que el sistema sufra una sobrecarga de solicitudes como ocurrió en la realidad, por lo que la aplicación deberá evitar que esto ocurra. Además, el sistema deberá actuar en base a un criterio de optimización definido, por lo que sus interacciones deberán procurar el cumplimiento de este.

Los recursos identificados en el problema son las vacunas que dispone el país, el cual es un valor que puede variar a medida que llegan vacunas al país en distintos momentos, también identificamos los vacunatorios que posee el país, ubicados en los distintos departamentos, así como también las diferentes vías para agendarse. Respecto a los procesos, observamos que en algún momento se deberá administrar las vacunas entrantes, también crear y administrar solicitudes de personas ingresando datos simultáneamente, agendar a las personas basándonos en sus solicitudes y respetando la disponibilidad de vacunas así como también respetando su riesgo. Por otro lado, realizar informes sobre las vacunas requeridas en los distintos vacunatorios en función de la demanda, y para finalizar, hacer observaciones estadísticas con el propósito de medir la solución implementada y así determinar el cumplimiento o no del objetivo.

# Manual de usuario

La simulacion funciona en base a los siguientes parametros configurables en el archivo src/Program/Main.java
- Cantidad de dias transcurridos en la simulacion
- Cantidad de hilos Agendadores
- Cantidad de hilos Despachadores por archivo de entrada de solicitudes
- Cantidad de hilos Estadisticos

Son necesarios los siguientes archivos de entrada (los cuales pueden ser creados randomicamente para ejecutar la simulacion):
1) Archivos de entrada con informacion de solicitudes de una cierta plataforma:
- WhatsApp: entradaWSP.txt
- App COVID-UY: entradaAPP.txt
- SMS via linea 08001919: entradaSMS.txt
- Pagina web: entradaWeb.txt

2) Archivos de entrada sobre el entorno fisico
- Vacunas de cada dia: entradaVacunas.txt
- Vacunatorios de cada departamento y su respectiva capacidad: vacunatoriosReal.txt

Integrantes:
- Paolo Mazza
- Sebastian Mazzey
- Nicolas Puig
