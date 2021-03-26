import pandas as pd
#import xlrd 
#import et_xmlfile
import openpyxl
from os.path import dirname, join


#accessing excel file using data frame
filename = join(dirname(__file__), "soildata.xlsx")
df = pd.read_excel(filename, engine='openpyxl')
df.to_numpy()

#search the crops throught places
def SearchByLocation():
    ans=(df.crops[(df.area == 'Navsari'.lower())])
    result=",".join(ans)
    return result

def SearchByCrop(Crop):
    return None
#choice the function call    
def main():
    return SearchByLocation()
    
print(main())