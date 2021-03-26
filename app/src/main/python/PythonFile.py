import numpy as np
import pandas as pd
import xlrd 
import et_xmlfile
import openpyxl
from os.path import dirname, join


#accessing excel file using data frame
filename = join(dirname(__file__), "soilexcel.xlsx")
df = pd.read_excel("soilexcel.xlsx", engine='openpyxl')
df.to_numpy()
ans=(df.crops[(df.area == 'Navsari'.lower())])
result=",".join(ans)
print(result)
