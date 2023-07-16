# CatGallery

Esta aplicacion es una galeria de busqueda de imagenes de gatos por raza. Para ello, se uso la API "TheCatApi".

CONSUMO DEL API: 

El BASE URL que utilice: https://api.thecatapi.com/v1/
Una vez generada el API KEY, se aplico la misma en el consumo del API para asi lograr tener acceso a toda la informacion del API.
Se hace una primera llamada al servicio para obtener el "breeds_id" de la raza, y un segundo llamado, donde con ese "breed_id" se obtiene la informacion
de la raza solicitada por el usuario en el campo de busqueda.

FUNCIONES DE LA APP:

El proyecto se realizo con una arquitectura MVVM, para que, si a futuro se desea ampliar el formato de la aplicacion, el codigo sea mas simple de reutilizar.
Las imagenes obtenidas se limitaron a un maximo de 10, para una carga rapida y efectiva de las mismas, en el RecyclerView. Si el usuario asi lo desea,
al hacer tap sobre una imagen, la misma se abre a pantalla completa en un nuevo Fragment, donde puede decidir, a traves de cada Button, si desea "Descargar" la imagen,
"Volver atras" al listado anteriormente arrojado por el RecyclerView, o "Volver al Inicio" al MainActivity para realizar otra busqueda.
Tambien, se implemento un sistema de cache con un tamaño maximo de 10MB. Las respuestas exitosas de las solicitudes se almacenarán en la caché y 
se podrán utilizar en futuras solicitudes si son válidas.

DEPENDENCIAS UTILIZADAS:

*Retrofit
*Glide
*Okhttp
