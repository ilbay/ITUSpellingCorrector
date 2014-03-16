# -*- coding: utf-8 -*-
gencor = open('gencor.txt',u'r')
newscor = open('newscor.txt',u'r')
output = open('odtu_corpus.txt',u'w')

#notcontain = ['<', '>', '#', '@', ''', '.', '?', ' ', '_', '-', '+', '$', ':', ',u', ';', '\'', ')', '(', '[', ']', '*', '&', '%', '!', '/', '|', '’', '½', '¤']

alphabet = [u'A',u'B',u'C',u'Ç',u'D',u'E',u'F',u'G',u'Ğ',u'H',u'I',u'İ',u'J',u'K',u'L',u'M',u'N',u'O',u'Ö',u'P',u'R',u'S',u'Ş',u'T',u'U',u'Ü',u'V',u'Y',u'Z',u'W',u'X',u'Q',u'a',u'b',u'c',u'ç',u'd',u'e',u'f',u'g',u'ğ',u'h',u'ı',u'i',u'j',u'k',u'l',u'm',u'n',u'o',u'ö',u'p',u'r',u's',u'ş',u't',u'u',u'ü',u'v',u'y',u'z',u'q',u'w',u'x',u'0',u'1',u'2',u'3',u'4',u'5',u'6',u'7',u'8',u'9']

gencorLines = gencor.readlines()
newscorLines = newscor.readlines()

for line in gencorLines:
    line = line.replace('\n','').decode("utf-8","ignore")
    contains = True
    for c in line:
        if c not in alphabet:
            contains = False
            break
    if contains == True and line != '':
        output.write(line.encode("utf-8","ignore")+'\n')

for line in newscorLines:
    line = line.replace('\n','').decode("utf-8","ignore")
    contains = True
    for c in line:
        if c not in alphabet:
            contains = False
            break
    if contains == True and line != '':
        output.write(line.encode("utf-8","ignore")+'\n')

gencor.close()
newscor.close()
output.close()