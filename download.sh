#!/bin/bash
curl ftp://ftp.monash.edu.au/pub/nihongo/JMdict_e.gz | gunzip > JMdict_e.xml
curl http://ftp.monash.edu/pub/nihongo/JMnedict.xml.gz | gunzip > JMnedict.xml
