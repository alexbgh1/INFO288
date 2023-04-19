# Problema 1 (HTTP)

Buscador de retail distribuido por categoría, este sistema debe ser implementado en **Python**.

## Esquema maestro esclavo

Un buscador distribuido, el cual permite distribuir la carga del trabajo desde un nodo maestro a diferentes nodos esclavos los cuales deben implementar las búsquedas sobre una base de datos (puede ser un json), la distribución de datos debe ser realizada por categoría de productos y por al menos de presentar 3 categorías, es decir, el esquema debe presentar por lo menos 3 esclavo. Las operaciones a implementar son:

### a) Búsqueda por producto

En el siguiente ejemplo estoy buscando "pantalon" y "camisa". Aquí debe implementar una función tipo broadcast, es decir, el maestro recibe la consulta, realiza la consulta sobre todos los esclavos, luego recibe la respuesta desde los esclavos y entrega el resultado en un arreglo con los datos encontrados.

```
http://localhost/xxx/query?productos=pantalón+camisa
```

### b) Búsqueda por categoría

Como el maestro conoce la distribución de la carga por categoría, la consulta debe ir dirigida a sólo los esclavos que contienen la información, luego debe reunir las respuestas desde los esclavos y entregar el resultado de un arreglo con los datos encontrados

```
http://localhost/xxx/query?categorias=ropa+computación
```

## Requisitos

El maestro conoce la distribución por categoría alojadas en los sistemas esclavos

La base de datos está conformada por archivos de texto que deben ser leídos al momento de ejecutar los esclavos, estos archivos pueden ser: **csv, json, etc**.

## Ejecución

conda create --name mi_entorno python=3.9

main.py > controller > common
