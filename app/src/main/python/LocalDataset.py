import pandas as pd
import openpyxl
from os.path import dirname, join


#accessing excel file using data frame
#for crops
filenameC = join(dirname(__file__), "soildata.xlsx")
df = pd.read_excel(filenameC, engine='openpyxl')
df.to_numpy()

#for soil
filenameS = join(dirname(__file__), "soildata.xlsx")
df = pd.read_excel(filenameS, engine='openpyxl')
df.to_numpy()

#search the crops throught places
def SearchByLocation(place):
    #place="abcd"
    ans=(df.crops[(df.area == place.lower())])
    if ans.empty:
        result="No data Available"
    else:
        result="\n".join(ans)
    return result

def SearchByCrop():
    Crop="carrot"
    ans=(df.soiltype[(df.crops == Crop.lower())])
    if ans.empty:
        result="No data Available"
    else:
        result="\n".join(ans)
    return result
#choice the function call    

    
print(SearchByLocation("navsari"))
print(SearchByCrop())