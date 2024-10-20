# Signal Detector

Это приложение позволяет отслеживать уровень сигнала мобильной связи и текущее местоположение вашего устройства. В настоящее время оно находится в процессе разработки и работает только с данными, полученными от сети LTE.

## Общая информация о приложении
- Язык программирования — **Kotlin**
- Для получения информации о мобильной связи используется — **TelephonyManager**
- Работа с местоположением осуществляется при помощи — **LocationManager**

## Какие данные может отслеживать приложение?

**Данные о сигнале мобильной связи:**
- `RSRP` — показывает среднее значение принимаемых абонентским устройством сигналов с базовой станции.
- `RSRQ` — указывает на качество принятых пилотных сигналов от текущей базовой станции.
- `RSSI` — значение мощности сигнала, поступающего на антенны устройства.
- `ASU Level` — выводит значение RSRP в другой системе показателя уровня принимаемого сигнала (ASU)
- `Level` — абстрактное значение уровня для общего качества сигнала.

**Общие данные об обслуживающей ячейке:**
- `Operator` — оператор мобильной связи.
- `Mnc` — код мобильной сети (оператора).
- `Mcc` — мобильный код страны.
- `Bandwidth` — ширина полосы ячейки в кГц.

**Данные о местоположении устройства:**
- `Latitude` — показывает значение широты.
- `Longitude` — показывает значение долготы.
  
## Внешний вид приложения
<img src="https://github.com/user-attachments/assets/9d289d04-7bce-4d19-96f2-11288c401d2e" width="300" />

## Запуск приложения
### Что необходимо для запуска приложения Signal Detector:
- Смартфон на базе Android
- Минимальная версия Android — 10
- Android Studio последних версий

### Установка приложения:

1. Скопируйте репозиторий на свой компьютер:
   ```bash
   git clone https://github.com/IvanNoritsin/Signal_Detector_App.git
   ```
2. Запустите Android Studio и откройте в нём скопированный проект
3. Подключите своё устройство к компьютеру
4. Запустите проект
5. На вашем устройстве будет установлено приложение Signal Detector
6. Откройте его и дайте согласие на все запрашиваемые разрешения

