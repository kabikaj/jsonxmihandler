# Copyright 2017 Alicia Gonzalez Martinez
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing
# permissions and limitations under the License.
#
######################################################################

INP_PARENT_DIR=../../data/files/inprogress
INP_OCRED_DIR=$(INP_PARENT_DIR)/ocred_texts
INP_HADITH_DIR=$(INP_PARENT_DIR)/hadith_alislam
INP_ALTAFSIR_DIR=$(INP_PARENT_DIR)/altafsir

OUT_PARENT_DIR=../../data/files/jsondump
OUT_OCRED_DIR=$(OUT_PARENT_DIR)/ocred_texts
OUT_HADITH_DIR=$(OUT_PARENT_DIR)/hadith_alislam
OUT_ALTAFSIR_DIR=$(OUT_PARENT_DIR)/altafsir

RM=/bin/rm -f

JAVA=java -cp
PKG=jsonxmihandler
JAR=target/dependency/*:target/$(PKG)-0.0.1-SNAPSHOT.jar

.PHONY : clean help all convert_ocred convert_hadith convert_altafsir


help:
	@echo "    all"
	@echo "        Clean resources and convert xmi files into json"
	@echo "    convert_ocred"
	@echo "        convert only sources from ocred texts"
	@echo "    convert_hadith"
	@echo "        convert only sources from hadith alislam"
	@echo "    convert_altafsir"
	@echo "        convert only sources from altafsir"
	@echo "    clean"
	@echo "        Clean resources"
	@echo ""
	@echo "usage: make [help] [clean]"

all: clean convert_ocred convert_hadith convert_altafsir

convert_ocred:
	$(JAVA) $(JAR) $(PKG).Pipeline -i "$(INP_OCRED_DIR)/*.xmi" -o $(OUT_OCRED_DIR) --to json

convert_hadith:
	$(JAVA) $(JAR) $(PKG).Pipeline -i "$(INP_HADITH_DIR)/*.xmi" -o $(OUT_HADITH_DIR) --to json

convert_altafsir:
	$(JAVA) $(JAR) $(PKG).Pipeline -i "$(INP_ALTAFSIR_DIR)/*.xmi" -o $(OUT_ALTAFSIR_DIR) --to json

clean:
	mvn clean dependency:copy-dependencies package
	$(RM) $(OUT_OCRED_DIR)/*.json
	$(RM) $(OUT_HADITH_DIR)/*.json
	$(RM) $(OUT_ALTAFSIR_DIR)/*.json
