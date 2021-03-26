import numpy as np
import pandas as pd
import xlrd 
import et_xmlfile

def main():
    df = pd.read_excel("D:\Raju\Python\project\datasets\Crop-wise_State-wise_Land_holdings_Area_Number.xlsx", engine='openpyxl')
    return df.columns
print(main())