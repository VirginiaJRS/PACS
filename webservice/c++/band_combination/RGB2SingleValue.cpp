/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
# Copyright (C) 2015 CSISS, GMU (http://csiss.gmu.edu), Ziheng Sun (szhwhu@gmail.com)
#
# This source is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.
#
# This code is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# A copy of the GNU General Public License is available on the World Wide Web
# at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by writing
# to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
# MA 02111-1307, USA.
#
#******************************************************************************
*/

/**********************************************************************/
/* 
   Image Analysis Library                

   \/\/\/\/\/\/\/\/\/\/

     

   \/\/\/\/\/\/\/\/\/\/

   Created time : Nov 25, 2013 .

   Developed by : Ziheng Sun, Hui Fang. Contact us: zsun@gmu.edu .

   \/\/\/\/\/\/\/\/\/\/

*/
/***********************************************************************/

#include <iostream>

#include "gdal_priv.h"
#include "ogrsf_frmts.h" //for ogr
#include "gdal_alg.h"  //for GDALPolygonize
#include "cpl_conv.h" //for CPLMalloc() 

int ImageRGB2SingleValue(const char * pszSrcFile,const char* pszDstFile,const char* pszFormat){
	GDALAllRegister();
	CPLSetConfigOption("GDAL_FILENAME_IS_UTF8","NO");

	GDALDataset* poSrcDS=(GDALDataset*)GDALOpen(pszSrcFile,GA_ReadOnly);
	if(poSrcDS==NULL)
	{
		return 0;
	}
	int width = poSrcDS->GetRasterXSize();
   	int height = poSrcDS->GetRasterYSize();
	int bandcount = poSrcDS->GetRasterCount();
	double adfgeotransform[6];
   	poSrcDS->GetGeoTransform(adfgeotransform);
	const char* projection = poSrcDS->GetProjectionRef();

	std::cout << "    Width:" << width << ";\n    Height:" << height << ";\n    bandcout:"<< bandcount << "\n";   
	if(bandcount!=3)
	{
		std::cout<<"Err: The inputted image doesn't have three bands.\n";
		return 0;		
	}
	std::cout << "--Read the raw image into memory\n";
	char* rawImg = new char[height*width*bandcount];//Open a memory space for saving the image
   	poSrcDS->RasterIO(GF_Read,0,0,width,height,rawImg,width,height,GDT_Byte,bandcount,NULL,GDT_Byte*bandcount,GDT_Byte*width*bandcount, GDT_Byte);
	int* img = new int[width*height];
	
	for(int i=0;i<width;i++){
		for(int j=0;j<height;j++){
			char red = rawImg[(j*width+i)*3];
			char green = rawImg[(j*width+i)*3+1];
			char blue = rawImg[(j*width+i)*3+2];
			int rgb = red;
			rgb = (rgb << 8) + green;
			rgb = (rgb << 8) + blue;
			img[j*width+i] = rgb;
		}
	}
	GDALDriver * driver=GetGDALDriverManager()->GetDriverByName(pszFormat);;
	GDALDataset * single_out = driver->Create(pszDstFile,width,height,1,GDT_Int32,NULL);
	single_out->SetGeoTransform(adfgeotransform);
	single_out->SetProjection(projection);

   	single_out->RasterIO(GF_Write,0,0,width,height,img,width,height,GDT_Int32,1,0,0,0,0);
	std::cout << "Combination is over.\n" ;
	GDALClose(poSrcDS); 
	GDALClose(single_out);//close the file
	
	return 1;
} 

int main(int argc, const char* argv[]){
	const char* pszSrcFile="";
   	const char* pszDstFile="";
	const char* outputFormat="";
	for(int i=0;i<argc;i++){
		if (strcmp(argv[i],"-i")==0) {
     			pszSrcFile = argv[i+1];
        		printf("input filename: %s\n",pszSrcFile);	
		}else if(strcmp(argv[i],"-o")==0){
			pszDstFile = argv[i+1];
        		printf("output filename: %s\n",pszDstFile);
		}else if(strcmp(argv[i],"-f")==0){
			outputFormat = argv[i+1];
        		printf("output format: %s\n",outputFormat);
		}
   	}
   	if(strlen(pszSrcFile)==0){
		std::cout<<"Err: Input image is missing."<<"\n";
		return 1;
   	}
	if(strlen(pszDstFile)==0){
		std::cout<<"Err: The file path of the vector is missing."<<"\n";
		return 1;
   	}
   	if(strlen(outputFormat)==0){
		std::cout<<"Err: The format of the vector is missing."<<"\n";
		return 1;
   	}
   	ImageRGB2SingleValue(pszSrcFile,pszDstFile,outputFormat);
  	// system("pause");
   	return 0;
}

