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

   Transform a raster into vector, realized by the GDALPolygonize() method of GDAL/OGR.

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

int EliminateSmallPolygons(const char * pszSrcFile,const char* pszDstFile,const char* pszFormat, int threshold_size){
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
	if(bandcount!=1)
	{
		std::cout<<"Err: The inputted image doesn't have only one band.\n";
		return 0;		
	}
	std::cout << "--Read the only band of the raw image\n";
	GDALRasterBand* sband = poSrcDS->GetRasterBand(1);
	
	GDALDriver * driver=GetGDALDriverManager()->GetDriverByName(pszFormat);;
	GDALDataset * single_out = driver->Create(pszDstFile,width,height,1,GDT_Int32,NULL);
	single_out->SetGeoTransform(adfgeotransform);
	single_out->SetProjection(projection);
	GDALRasterBand* tband = single_out->GetRasterBand(1);
	
	std::cout << "--Filter small polygons..\n";
	GDALSieveFilter(sband,NULL,tband,threshold_size,4,NULL,NULL,NULL);
	std::cout << "--Filter Over.\n";	
	std::cout << "--Close the opened GDAL datasets.\n";
	GDALClose(poSrcDS); 
	GDALClose(single_out);//close the file
	
	return 1;
} 

int main(int argc, const char* argv[]){
	const char* pszSrcFile="";
   	const char* pszDstFile="";
	const char* outputFormat="";
  	int size = -1;
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
		}else if(strcmp(argv[i],"-size")==0){
                 	size = atoi(argv[i+1]);	
                }else if(strcmp(argv[i],"--help")==0){
			std::cout << "--help \n";
			std::cout << "EliminateSmallPolygon's Usage : \n";
			std::cout << "    [-i  input file path;]\n";
			std::cout << "    [-o  destination file path]\n";
			std::cout << "    [-f  output file format,e.g. GTiff]\n";
			std::cout << "    [-size  the threshold for the small-size polygons. If the size of polygons in the input image is smaller than the value, the polygons will be merged into the neighbor large polygons.]\n";
                        std::cout << "    [--help  Look at the introductions of the Eliminate command.]\n";
			return 1;
		}
   	}
   	if(strlen(pszSrcFile)==0){
		std::cout<<"Err: Input image is missing."<<"\n";
		return 1;
   	}
	if(strlen(pszDstFile)==0){
		std::cout<<"Err: The file path of the output image is missing."<<"\n";
		return 1;
   	}
   	if(strlen(outputFormat)==0){
		std::cout<<"Err: The format of the output image is missing."<<"\n";
		return 1;
   	}
	if(size==-1){
		std::cout << "Warning: The threshold size of the small polygons is not set. Default value 10 is used.";
                size = 10;
	}
   	EliminateSmallPolygons(pszSrcFile,pszDstFile,outputFormat,size);
  	// system("pause");
   	return 0;
}


