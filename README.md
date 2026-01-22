# 🖼️ Pavimento IMG

Módulo de **procesamiento y preparación de imágenes** para el análisis de pavimento vial.  
Este proyecto **no clasifica estados** ni toma decisiones; su función es **preparar, limpiar y medir** las imágenes antes de ser analizadas por el sistema de IA principal (`pavimento-ia`).

---

## 🎯 Objetivo

- Procesar imágenes de vías (panorámicas o normales).
- Validar calidad, formato y resolución.
- Normalizar imágenes para modelos de visión por computador.
- Extraer métricas visuales base.
- Entregar imágenes listas para:
  - Segmentación de pavimento
  - Detección de daños
  - Clasificación de estado

---

## 🧠 Tecnologías utilizadas

- **Python 3.11**
- **OpenCV**
- **Pillow**
- **NumPy**
- **TorchVision**
- **Shapely** (opcional, para geometría)
- **GDAL** (opcional, para coordenadas / SIG)

---

## 📂 Estructura del proyecto

```json
pavimento-img/
├── image_processor/
│   ├── loader.py        # carga de imágenes
│   ├── validator.py     # validación de formato y calidad
│   ├── preprocess.py   # limpieza y ajustes iniciales
│   ├── normalizer.py   # resize, orientación, escala
│   ├── roi.py          # región de interés (calzada)
│   └── metrics.py      # métricas visuales
├── inputs/
│   └── images/          # imágenes de entrada
├── outputs/
│   └── processed/       # imágenes procesadas
├── requirements.txt
└── README.md
````

---

## ⚙️ Instalación

Se recomienda usar entorno virtual.

```bash
pip install -r requirements.txt
````

---

## ▶️ Uso básico

Ejemplo de ejecución directa del procesamiento:

```bash
python image_processor/loader.py \
  --input inputs/images \
  --output outputs/processed
```

---

## 🔄 Flujo de procesamiento

1. **Carga de imágenes**
2. **Validación**

    * formato
    * resolución
    * peso
3. **Preprocesamiento**

    * corrección de orientación
    * eliminación de ruido básico
4. **Normalización**

    * resize
    * escalado
5. **ROI**

    * detección de zona de calzada
6. **Métricas**

    * porcentaje de pavimento visible
    * resolución efectiva
    * calidad de imagen
7. **Salida**

    * imagen lista para IA

---

## 📊 Métricas generadas

* Porcentaje de pavimento visible
* Área útil de análisis
* Resolución efectiva
* Calidad de imagen (baja / media / alta)
* Indicadores base para detección de daño

---

## 🔗 Integración

Este módulo es consumido por:

* **pavimento-ia**
* Procesos batch (ZIP / KMZ)
* Sistemas SIG / GIS
* Flujos predictivos de mantenimiento vial

---

## ⚠️ Consideraciones

* No realiza clasificación de estado.
* No genera recomendaciones.
* No reemplaza evaluación técnica.
* Funciona como **módulo de soporte visual**.

---

## 📌 Estado del proyecto

* Procesamiento base: ✅ operativo
* ROI de calzada: ✅ implementado
* Métricas visuales: ✅ implementadas
* Optimización batch/GPU: 🔜 planificado

