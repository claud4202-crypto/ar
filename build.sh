#!/usr/bin/env bash
set -euo pipefail
#
# Билд обоих плагинов одним прогоном:
#   ./build.sh
#
# Результат:
#   ArisDonate/target/ArisDonate-1.0.0.jar
#   RegionBlocks/target/RegionBlocks-1.0.0.jar
#
# Требуется: JDK 21+ и Maven 3.6+
#
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# install (а не package), чтобы RegionBlocks мог разрешить зависимость
# me.arisdonate:ArisDonate:1.0.0 из локального ~/.m2 при отдельной сборке.
mvn -B clean install -DskipTests

echo
echo "Готово! Положи на сервер ОБА .jar в plugins/:"
ls -lh "$SCRIPT_DIR/ArisDonate/target/ArisDonate-1.0.0.jar" \
       "$SCRIPT_DIR/RegionBlocks/target/RegionBlocks-1.0.0.jar"
