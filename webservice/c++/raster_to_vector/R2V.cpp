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

/**
* Input a one-band image and output the polynized vector.
* The polygons in the vector has only one property "value" which 
* contains the value for the pixels in the polygon.
*/
int ImagePolygonize(const char * pszSrcFile,const char* pszDstFile,const char* pszFormat)
{
	GDALAllRegister();
	OGRRegisterAll();//register ogr
	CPLSetConfigOption("GDAL_FILENAME_IS_UTF8","NO");

	GDALDataset* poSrcDS=(GDALDataset*)GDALOpen(pszSrcFile,GA_ReadOnly);
	if(poSrcDS==NULL)
	{
		return 0;
	}
	// get vector driver
	OGRSFDriver *poDriver;
	poDriver = OGRSFDriverRegistrar::GetRegistrar()->GetDriverByName( pszFormat );
	if (poDriver == NULL)
	{  
		GDALClose((GDALDatasetH)poSrcDS); 
		return 0;
	}
	// create output vector
	OGRDataSource* poDstDS=poDriver->CreateDataSource(pszDstFile);
	if (poDstDS==NULL)
	{
		GDALClose((GDALDatasetH)poSrcDS);
		return 0;
	}
	// define the spatial projection of the output vector, the same as the input image
	//note: if the image has no projection, the vector will be mirror-like with the image.
	std::cout << "Set the projection of the image to the vector.\n";
	OGRSpatialReference *poSpatialRef = new OGRSpatialReference(poSrcDS->GetProjectionRef());
	OGRLayer* poLayer = poDstDS->CreateLayer("Vector", poSpatialRef, wkbPolygon, NULL);
	if (poDstDS == NULL) 
	{
		GDALClose((GDALDatasetH)poSrcDS); 
		OGRDataSource::DestroyDataSource(poDstDS); 
		delete poSpatialRef; 
		poSpatialRef = NULL; 
		return 0;
	}
	std::cout << "Create a field : Segment\n";
	OGRFieldDefn ofieldDef("Segment", OFTInteger); //create property table for the output vector
	poLayer->CreateField(&ofieldDef);
        std::cout << "Create a field : Id\n";
	OGRFieldDefn oField( "Id", OFTInteger );
    	if( poLayer->CreateField( &oField ) != OGRERR_NONE )
    	{
        	printf( "ERR: Creating Id field failed.\n" );
        	exit( 1 );
    	}

	GDALRasterBandH hSrcBand = (GDALRasterBandH) poSrcDS->GetRasterBand(1); //get the first band of the image
	std::cout << "Polygonizing the image..\n";
	GDALPolygonize(hSrcBand, NULL, (OGRLayerH)poLayer, 0, NULL, NULL, NULL); //invoke the function polygonize
        //set an id to each feature
	OGRFeature *poFeature;
	poLayer->ResetReading();
	int idnum = 0;
	while( (poFeature = poLayer->GetNextFeature()) != NULL )
	{
		poFeature->SetField("Id",idnum++);
                poLayer->SetFeature(poFeature);
	}
	
        std::cout << "The process is over. Releasing the resources..\n";
	GDALClose(poSrcDS); //close the file
	OGRDataSource::DestroyDataSource(poDstDS);
	return 1;
}

int main(int argc, char* argv[])
{
   //const char* pszSrcFile = "/media/szh/728dcb04-ab21-4f6f-a472-bdc08e2729c21/c++/feature_extraction/test.fusion.jpg";
   //const char* pszDstFile = "/media/szh/728dcb04-ab21-4f6f-a472-bdc08e2729c21/c++/feature_extraction/shape.shp";
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
	}else if(strcmp(argv[i],"--help")==0){
                std::cout<<"Info [-i : input file path]\n";
		std::cout<<"     [-o : output file path]\n";
		std::cout<<"     [-f : output file format, like \"ESRI Shapefile\"]"<<"\n";
                return 1;
        }
   }
   if(strlen(pszSrcFile)==0){
	std::cout<<"Err: Input image is missing."<<"\n";
        std::cout<<"Info[-i : input file path][-o : output file path][-f : output file format, like \"ESRI Shapefile\"]"<<"\n";
	return 1;
   }
   if(strlen(pszDstFile)==0){
	std::cout<<"Err: The file path of the vector is missing."<<"\n";
        std::cout<<"Info[-i : input file path][-o : output file path][-f : output file format, like \"ESRI Shapefile\"]"<<"\n";
	return 1;
   }
   if(strlen(outputFormat)==0){
	std::cout<<"Err: The format of the vector is missing."<<"\n";
        std::cout<<"Info[-i : input file path][-o : output file path][-f : output file format, like \"ESRI Shapefile\"]"<<"\n";
	return 1;
   }
   ImagePolygonize(pszSrcFile,pszDstFile,outputFormat);
  // system("pause");
   return 0;
}

