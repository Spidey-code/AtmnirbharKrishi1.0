import pandas as pd
import openpyxl
from os.path import dirname, join


#accessing excel file using data frame
filename = join(dirname(__file__), "soildata.xlsx")
df = pd.read_excel(filename, engine='openpyxl')
df.to_numpy()

#search the crops throught places
def SearchByLocation(place):
    #place="abcd"
    ans=(df.crops[(df.area == place.lower())])
    if ans.empty:
        result="No data Available"
    else:
        result=" ".join(ans)
    return result

def SearchByCrop(Crop):
    return None
#choice the function call    

    
print(SearchByLocation("navsari"))