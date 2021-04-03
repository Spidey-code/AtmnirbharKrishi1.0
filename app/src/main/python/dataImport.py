import numpy as np
import pandas as pd
import xlrd 
import et_xmlfile

def main():
<<<<<<< HEAD
    df = pd.read_excel("Crop-wise_State-wise_Land_holdings_Area_Number.xlsx", engine='openpyxl')
=======
    df = pd.read_excel("D:\Raju\Python\project\datasets\Crop-wise_State-wise_Land_holdings_Area_Number.xlsx", engine='openpyxl')
>>>>>>> b7bb557bd409b72a8e48a8aef919f40452ca84ea
    return df.columns
print(main())