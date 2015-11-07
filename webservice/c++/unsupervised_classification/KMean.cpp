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

   KMean Image Clustering

   \/\/\/\/\/\/\/\/\/\/

   Created time : 2014.6.30

   Developed by : Ziheng Sun, Michael You, Contact us: zsun@gmu.edu.

   \/\/\/\/\/\/\/\/\/\/

*/
/***********************************************************************/
#include <iostream>

#include <math.h>
#include <vector>
#include "gdal_priv.h"
#include "gdal_alg.h"  //for GDALPolygonize
#include "cpl_conv.h" //for CPLMalloc() 

//accessory methods
int ifFarEnough(int* array, int x, int y, int d, int limit)
{	
	
	for(int i=0; i<limit; i++)
	{
		if( (pow( (array[2*i]-x), 2) + pow( (array[2*i+1]-y), 2) ) < pow(d, 2))
			return 0;

	}
	return 1;
}

int generateX(int width)
{
	return rand()%width;
}
int generateY(int height)
{
	return rand()%height;
}

struct point{
 int x;
 int y;
 int r;
 int b;
 int g;

};
struct pointDouble{
 double x;
 double y;
 double r;
 double b;
 double g;
};
void set(point p1, point p2)
{
	p2.x = p1.x;
	p2.y = p1.y;
	p2.r = p1.r;
	p2.g = p1.g;
	p2.b = p1.b;
}
struct cluster{
 
 point initial;
 std::vector<point> array;
};
/*
double calcDis(std::vector<point> v,  point p2)
{
	double total = 0.0;
	point mpoint;
	mpoint.x = 0;
	mpoint.y = 0;
	mpoint.r = 0;
	mpoint.g = 0;
	mpoint.b = 0;
	for(std::vector<point>::iterator it = v.begin(); it != v.end(); ++it)
	{
		point p1 = (point)*it;
		mpoint.x += p1.x;
		mpoint.y += p1.y;
		mpoint.r += p1.r;
		mpoint.g += p1.g;
		mpoint.b += p1.b;
	}
	mpoint.x = mpoint.x/v.size();
	mpoint.y = mpoint.y/v.size();
	mpoint.r = mpoint.r/v.size();
	mpoint.g = mpoint.b/v.size();
	mpoint.b = mpoint.g/v.size();
	
		
	//	total += (pow( (mpoint.x-p2.x), 2) +
	//		pow( (mpoint.y-p2.y), 2) +
	//		pow( (mpoint.r-p2.r), 2) +
	//		pow( (mpoint.g-p2.g), 2) +
	//		pow( (mpoint.b-p2.b), 2) );
	total += (pow( (mpoint.r-p2.r), 2) +
			pow( (mpoint.g-p2.g), 2) +
			pow( (mpoint.b-p2.b), 2) );
	

	return total;
}
*/
double calcDisPoint(pointDouble p1, point p2)
{
	return  //s//qrt(pow( (p1.x-p2.x), 2) +
			//pow( (p1.y-p2.y), 2) ) 
			 
			pow( (p1.r-p2.r), 2) +
			pow( (p1.g-p2.g), 2) +
			pow( (p1.b-p2.b), 2)  ;
}
pointDouble averagePoints(std::vector<point> v)
{
	pointDouble temp;
	for(std::vector<point>::iterator it = v.begin(); it != v.end(); ++it)
	{
		point p1 = (point)*it;
		temp.x += p1.x;
		temp.y += p1.y;
		temp.r += p1.r;
		temp.g += p1.g;
		temp.b += p1.b;
	}
	temp.x = temp.x/v.size();
	temp.y = temp.y/v.size();
	temp.r = temp.r/v.size();
	temp.g = temp.b/v.size();
	temp.b = temp.g/v.size();

	return temp;
}
//add number
pointDouble averagePoints2(pointDouble p1, point p2,int n)
{
	pointDouble temp; 	
	//newp = p1*N/(N-1)+p2/N;
	//std::cout << "p1.r = "<< p1.r << "p2.r = "<< p2.r<<"\n";
	/*temp.x = p1.x*(n-1)/n+p2.x/n;//(p1.x + p2.x )/2.0 ;
	temp.y = p1.y*(n-1)/n+p2.y/n;//(p1.y + p2.y )/2.0 ;
	temp.r = p1.r*(n-1)/n+p2.r/n;//(p1.r + p2.r )/2.0 ;
	temp.g = p1.g*(n-1)/n+p2.g/n;//(p1.g + p2.g )/2.0 ;
	temp.b = p1.b*(n-1)/n+p2.b/n;//(p1.b + p2.b )/2.0 ;*/
	
	/*temp.x = (p1.x + p2.x )/2.0 ;
	temp.y = (p1.y + p2.y )/2.0 ;
	temp.r = (p1.r + p2.r )/2.0 ;
	temp.g = (p1.g + p2.g )/2.0 ;
	temp.b = (p1.b + p2.b )/2.0 ;*/

	//std::cout << "temp.r = "<< temp.r<<"\n";
	//return temp;
	return p1;
	
}
pointDouble copyPoint(point p1)
{
	pointDouble temp;
	//ziheng - 8/20/2014 - replace += with =
	temp.x = p1.x;
	temp.y = p1.y;
	temp.r = p1.r;
	temp.g = p1.g;
	temp.b = p1.b;
	return temp;
}
int colorDistance(cluster* clusters, point p, int limit)
{	
	
	for(int i=0; i<limit; i++)
	{
		if(clusters[i].initial.r == p.r && clusters[i].initial.b == p.b && clusters[i].initial.g == p.g)
			return 0;
	}
	return 1;
}
/**
K-mean
*/
int KMean(const char * pszSrcFile,const char* pszDstFile,const char* pszFormat, const char* kn)
{
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
	int k = atoi(kn);

	std::cout << "    Width:" << width << ";\n    Height:" << height << ";\n    bandcount:"<< bandcount << "\n";   
	if(bandcount!=3)
	{
		std::cout<<"Err: The inputted image doesn't have three bands.\n";
		return 0;		
	}
	std::cout << "--Read the raw image into memory\n";
	unsigned char* rawImg = new unsigned char[height*width*bandcount];//Open a memory space for saving the image
   	poSrcDS->RasterIO(GF_Read,0,0,width,height,rawImg,width,height,GDT_Byte,bandcount,NULL,GDT_Byte*bandcount,GDT_Byte*width*bandcount, GDT_Byte);

	
	//firststep
	srand(time(0));
	int* randomPoints = new int[2*k]; //get k random points that are spaced apart
	int distance = 5;                 //How close points are
	std::cout<<width<<"    "<<height<<"\n";

	cluster* clusters = new cluster[k];

	point first;

	first.x = generateX(width);
	first.y = generateY(height);
	first.r = (int)rawImg[(first.y*width+first.x)*3];
	first.g = (int)rawImg[(first.y*width+first.x)*3+1];
	first.b = (int)rawImg[(first.y*width+first.x)*3+2];

	clusters[0].initial = first;
	clusters[0].array.push_back(first);

	for(int i=0; i<k; i++)
	{
		//int randomX = (int)rand()%width;
		//int randomY = (int)rand()%height;

		point temp;
		//std::cout<<"X: "<<randomX<<"      Y: "<<randomY<<"\n";
		do {
			temp.x = generateX(width);
			temp.y = generateY(height);
			temp.r = (int)rawImg[(temp.y*width+temp.x)*3];
			temp.g = (int)rawImg[(temp.y*width+temp.x)*3+1];
			temp.b = (int)rawImg[(temp.y*width+temp.x)*3+2];
				
		//}while(ifFarEnough(randomPoints, temp.x, temp.y, distance, i-1)!=1 && colorDistance(clusters, temp, i-1) !=1 );
		}while(colorDistance(clusters, temp, k) !=1 ); //ziheng - 8/20/2014

		std::cout<<"output"<< (int)temp.r  <<"\n";
		//defines index and puts in the initial point
	
		//set(temp, clusters[i].initial);


		clusters[i].initial = temp;
		clusters[i].array.push_back(temp);


		//std::cout << "temp: x"<< temp.x << " " << temp.y <<"\n";		
		//std::cout << "initial : x"<< clusters[i].initial.x << " "<< clusters[i].initial.y<<"\n";
			
		
	}
	
	std::cout<<"testpoint1\n";
	//calculate the distance of each point to the k cluster points
	
	

	std::cout<<"testpoint2\n";
	pointDouble* avArray = new pointDouble[k];
	for(int i=0; i<k; i++)
	{
		pointDouble copy = copyPoint(clusters[i].initial);
		avArray[i] = copy;
		std::cout << "avarray["<<i<<"] : "<< avArray[i].r<<"\n";
	}
	for(int j = 0; j<height; j++)
	{
		for(int i=0; i<width; i++)
		{
			//gets a point, calculates the closest cluster, and adds it to the cluster
			point tp;
			tp.x = i;
			tp.y = j;
			tp.r= rawImg[(j*width+i)*3];
			tp.g = rawImg[(j*width+i)*3+1];
			tp.b = rawImg[(j*width+i)*3+2];
			
			double min = -1.0; 
			int minInd = -1.0;
			for(int l = 0; l<k; l++)
			{								
				double tempD = calcDisPoint(avArray[l], tp);
				if(tempD<min||min==-1.0){
					min = tempD;
					minInd = l;
				}
				//std::cout<<"TEMPD"<<tempD<<"      "<<"MinDistance: "<<min<<"  minIndex:   "<<minInd <<    "\n";
			}
			if(minInd==-1.0){
				throw "111";			
			}
			//std::cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
			//std::cout<<"MinDistance: "<<min<<"  minIndex:   "<<minInd <<    "\n";
			//std::cout<<"test"<<i<<" "<<j<<"\n";

			clusters[minInd].array.push_back(tp);
			if(avArray[minInd].r<1000)
			std::cout << "avarray["<<minInd<<"] : "<< avArray[minInd].r<<"cluster array size"<<clusters[minInd].array.size()<<"\n";
			avArray[minInd] = averagePoints2(avArray[minInd],tp,clusters[minInd].array.size());
			if(avArray[minInd].r<1000)	
			std::cout << "avarray["<<minInd<<"] : "<< avArray[minInd].r<<"\n";	
				
		}
	}
	std::cout<<"testpoint3\n";
	char* img = new char[width*height*bandcount];

	for(int a=0; a<k; a++)
	{
		std::vector<point> v = clusters[a].array;
			int red = clusters[a].initial.r;
			int green = clusters[a].initial.g;
			int blue = clusters[a].initial.b;
			
		std::cout << "================================\nInitial point"<<red << " "<<green<<" "<<blue<<"\n";
		std::cout<<"size of vector: "<<v.size() <<"\n";
		for(std::vector<point>::iterator it = v.begin(); it != v.end(); ++it) 
		{
		     	
			int i = ((point)*it).x;
			int j = ((point)*it).y;
			img[(j*width+i)*3] = (char)red;
			img[(j*width+i)*3+1] = (char)green;
			img[(j*width+i)*3+2] = (char)blue;
			//std::cout << "================================\nInitial point"<<red << " "<<green<<" "<<blue<<"\n";
			//std::cout << "Point: x: "<<i << " y:"<<j<<" red:"<<img[(j*width+i)*3]<<"\n";
			
		}
	}	

	std::cout<<"testpoint4\n";
	GDALDriver * driver=GetGDALDriverManager()->GetDriverByName(pszFormat);;
	GDALDataset * single_out = driver->Create(pszDstFile,width,height,bandcount,GDT_Byte,NULL);
	single_out->SetGeoTransform(adfgeotransform);
	single_out->SetProjection(projection);

   	single_out->RasterIO(GF_Write,0,0,width,height,img,width,height,GDT_Byte,bandcount,NULL,GDT_Byte*bandcount,GDT_Byte*width*bandcount, GDT_Byte);
	
	GDALClose(poSrcDS); 
	GDALClose(single_out);//close the file
	
	return 1;
}



int main(int argc, char* argv[])
{
   //const char* pszSrcFile = "/media/szh/728dcb04-ab21-4f6f-a472-bdc08e2729c21/c++/feature_extraction/test.fusion.jpg";
   //const char* pszDstFile = "/media/szh/728dcb04-ab21-4f6f-a472-bdc08e2729c21/c++/feature_extraction/shape.shp";
   const char* pszSrcFile="";
   const char* pszDstFile="";
   const char* outputFormat="";
   const char* k="";
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
	}else if(strcmp(argv[i],"-k")==0){
		k = argv[i+1];
        	printf("k: %s\n",k);
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
   KMean(pszSrcFile,pszDstFile,outputFormat, k);
  // system("pause");
   return 0;
}

