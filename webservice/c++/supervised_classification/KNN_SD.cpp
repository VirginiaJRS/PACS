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
   Image Classification Library                

   \/\/\/\/\/\/\/\/\/\/
   
    This code library implements the calculation of the feature properties of the segmented object. The feature properties include three main classes: spectral, spatial and texture. Each class contains multiple kind of features. For example, the "spectral" class contains Mean(Layer 1,2,3), Standard Deviation(layer 1,2,3),etc. In this library, each feature is set an unique id. The feature "Spectral|Mean|Brightness"'s id is 111. The input feature list is a list of feature ids.This file requires not only the segmented Vector geometries, but also the origin images as inputs. The output file is a vector file in which the geometries are annotated by their feature property values.

   This file is based on the results of CalculateFeature.cpp and realizes the K nearest neighbour classification method. The method selects K samples which are nearest to the to-be-classified object, and judge which class shows up most in the K samples. Classify the object to the mostly occured class. This file is similar to the KNN.cpp. However, this file is based on the inputted samples from a sample database rather than the selected sample from the image itself. This is supposed to be able to make the whole classification workflow completely automatic.

  \/\/\/\/\/\/\/\/\/\/\/
  
  Created time: Feb 3, 2015.  
 
  Authors: Ziheng Sun (szhwhu@gmail.com), Hui Fang.
   
*/
/**********************************************************************/

#pragma once
#include <iostream>
#include <cstring>
#include <fstream>

#include "gdal_h/gdal_priv.h"//for gdal
#include "gdal_h/ogrsf_frmts.h" //for ogr
#include "Utils.h"

#include <math.h>

std::vector<char*> split(char* str,const char* divider){
        std::vector<char*> array;
        char * pch;
        printf ("Splitting string \"%s\" into tokens:\n",str);
        pch = strtok (str,divider);
        while (pch != NULL)
        {
                printf ("%s\n",pch);
                array.push_back(pch);
                pch = strtok (NULL, divider);
        }
        return array;
}

bool isSample(int fid, std::map<int,char*> samplemap){
	std::map<int,char*>::iterator sit = samplemap.find(fid);
	bool is =false;
	if(sit!=samplemap.end()){
		is = true;	
	}
	return is;
}

double distance(std::vector<double>* f1, std::vector<double>* f2){
	double totaldistance = 0.0;
	for(std::vector<double>::iterator f1it = f1->begin(), f2it = f2->begin(); f1it!=f1->end();f1it++,f2it++){
		double f1v = *f1it;
		double f2v = *f2it;
		totaldistance+=(f1v-f2v)*(f1v-f2v);
	}
	return sqrt(totaldistance);
}

int KNN_SD(char* vectorfilepath,char* outputfilepath, char* featurelist, char* kvalue, char* hierarchy,char* samplefilepath,char* noclass, char* threshold){
	OGRRegisterAll();
	CPLSetConfigOption("GDAL_FILENAME_IS_UTF8","NO");
	//open vector file
        OGRDataSource *poVDS;
        poVDS = OGRSFDriverRegistrar::Open( vectorfilepath, FALSE );
        if( poVDS == NULL )
        {
                std::cout << "Err: Fail to open the input vector file: " << vectorfilepath << "\n";
                return 1;
        }
	std::cout << "Parsing the input parameters.\n";
	//set class map
	std::map<int,char*> classmap;
	//get k
	std::string temp(kvalue);
	int k = Format::String2Int(temp);
	
	//get threshold
	std::string temp2(threshold);
	double thres = Format::String2Double(temp2);
	//get feature list
	const char* divider = ",";
	const char* divider2 = ":";
        std::vector<char*> features = split(featurelist,divider);
	//get class list
	std::vector<char*> classes = split(hierarchy,divider);
	//get sample list
	//added by ziheng - 2.3.2015
	const char* divider3 = "+";
	const char* divider4 = "=";
	const char* divider5 = ";";
	//update by ziheng - 2.3.2015
	//for the samples from database, they absolutely have different set of Identifiers. 
	//use "SD_" as the head to distinguish samples from the features in the vector.
	//read samples from sample file
	std::ifstream file(samplefilepath);
	std::string tp;
	std::getline(file, tp);
	std::cout << "Sample list: \n" << tp << "\n";	
	char *samples = (char*)tp.c_str();
	
	std::vector<char*> sams = split(samples,divider4);
	std::map<int,char*> samplemap; //sampleId -> class
	std::map<int,std::map<char*,double> > samplepropertymap ;//sampleId -> properties
	for(std::vector<char*>::iterator sit=sams.begin();sit!=sams.end();sit++){
		char* sam = *sit;
		std::vector<char*> idpropertiesclass = split(sam,divider3);
		char* sit_str = idpropertiesclass[0];
		int sid = Format::String2Int(sit_str);
		//strcat( sit_str, "SD"); //id
		//std::cout << "New Sample Name: " << sit_str << "\n";
		
		
		char* sprop = idpropertiesclass[1]; //properties
		std::vector<char*> props = split(sprop, divider5);
		std::map<char*, double> newid2propervaluemap;
		for(std::vector<char*>::iterator nit=props.begin();nit!=props.end();nit++){
			char* properties = *nit;
			std::vector<char*> propname2value = split(properties, divider2);
			char* fname = propname2value[0];
			double fv = Format::String2Double(propname2value[1]);
			newid2propervaluemap[fname]= fv;
		}
		samplepropertymap[sid] = newid2propervaluemap;
		
		char* sclas = idpropertiesclass[2]; //class
		//std::cout << "New Sample Class: " << sclas << "\n";
		samplemap[sid] = sclas;
	}
	
	//get the metadata of the vector file
        int layercount = poVDS->GetLayerCount();
        std::cout << layercount << "\n";
        OGRLayer* layer = poVDS->GetLayer(0);
	//get id field
        OGRFeatureDefn* fds = layer->GetLayerDefn();
        int idindex = fds->GetFieldIndex("Id");
	
	//get feature's field numbers
	std::vector<int> fnos;
	for(std::vector<char*>::iterator fit=features.begin();fit!=features.end();fit++ ){
		char* fname = *fit;
		
		int theid = fds->GetFieldIndex(fname);
		std::cout << " Feature field "<< fname <<"'s number:"<<theid<<"\n";
		if(theid==-1){
			std::cout << "ERR: Feature " << fname << "cann't be found in the input vector file's property table.";
			throw 511;
		}
		fnos.push_back(theid);
	}
	//get feature map of the features in the inputted vector
	std::map<int,std::vector<double>*> allfeaturemap;
	
	OGRFeature* tf;
	while((tf=layer->GetNextFeature())!=NULL){
		const char* field = tf->GetFieldAsString(idindex);
		int fid = Format::String2Int(field);
		//get all the feature values
		std::vector<double>* fvalues = new std::vector<double>();
		for(std::vector<int>::iterator it = fnos.begin();it!=fnos.end();it++){
			int fno = *it;
		
			double fvalue = tf->GetFieldAsDouble(fno);	
			//std::cout<<"Get Feature "<<fid<<"'s Property:"<<fno<<" "<<fvalue<<"\n" ;
			fvalues->push_back(fvalue);
		}
		allfeaturemap[fid] = fvalues;
	}
	
	//normalize the features into the scope of 0~1
	//first get the minimum and maximum value of each kind of feature
	std::cout << "===================\n  Normalizing the feature values..\n";
	int len = features.size();
	double* minfv = new double[len];
	double* maxfv = new double[len];
	int lab = -1;
	
	for(std::map<int,std::vector<double>*>::iterator ait = allfeaturemap.begin();ait!=allfeaturemap.end();ait++){		
		int fid = ait->first;
		std::vector<double>* fvalues = ait->second;
		if(lab==-1){
			for(int i=0;i<len;i++){
				double fv = fvalues->at(i);
				minfv[i] = fv;
				maxfv[i] = fv;
			}
			lab++;
		}
		//get the maximum and minimum value of the features in the inputted vector
		for(int i=0;i<len;i++){
			double fv = fvalues->at(i);
			if(fv>maxfv[i]){
				maxfv[i] = fv;
			}
			if(fv<minfv[i]){
				minfv[i] = fv;
			}
		}
	}
	
	for(int i=0;i<len;i++){
		std::cout<<"Maximum and Minimum: " << maxfv[i] << " - " << minfv[i] << "\n";
	}
	std::cout << "==========================================\nUpdated by samples\n=============================================";
	//traverse the sample set to get the overall maximum and minimum values
	//added by Ziheng - 2.3.2015
	for(std::map<int, std::map<char*, double> >::iterator sit = samplepropertymap.begin();sit!=samplepropertymap.end();sit++){
		
		int sid = sit->first;
		std::map<char*, double> pvmap = sit->second;
		int i=0;
		for(std::vector<char*>::iterator fit = features.begin();fit!=features.end();fit++,i++){
			char* f = *fit;
			double v = pvmap[f];
			if(v>maxfv[i]){
				maxfv[i] = v;		
			}
			if(v<minfv[i]){
				minfv[i] = v;
			}
		}
	}	
	for(int i=0;i<len;i++){
		std::cout<<"Maximum and Minimum: " << maxfv[i] << " - " << minfv[i] << "\n";
	}
	std::cout << "End\n" ;	

	double* maxmindis = new double[len];
	for(int i=0;i<len;i++){
		maxmindis[i] = maxfv[i]-minfv[i];
	}
	std::cout << "==>Update the properties of the features in the inputted vector.\n";
	for(std::map<int,std::vector<double>*>::iterator ait = allfeaturemap.begin();ait!=allfeaturemap.end();ait++){
		int fid = ait->first;
		std::vector<double>* fvalues = ait->second; 	
		for(int i=0;i<len;i++){
			double fv = fvalues->at(i);
			double newfv = (fv-minfv[i])/maxmindis[i];
			fvalues->at(i) = newfv;
			//std::cout<<"  New normalized feature: "<<newfv<<"\n";
		}
	}
	
	for(std::map<int, std::map<char*, double> >::iterator sit = samplepropertymap.begin();sit!=samplepropertymap.end();sit++){
		int sid = sit->first;
		std::map<char*, double> pvmap = sit->second;
		//std::cout<< "~~~~~~~~~~~~~~~~~~~~~~Feature " << sid << "\n";
		for(std::map<char*, double>::iterator fit = pvmap.begin();fit!=pvmap.end();fit++){
			char* f = fit->first;
			double v = fit->second;
			//std::cout << "Feature Id:" << f << "- " << v << "\n";
		}
		samplepropertymap[sid] = pvmap; //update the sample property map
	}
	//added by ziheng - 2.3.2015
	std::cout << "==>Update the properties of the samples.\n";
	for(std::map<int, std::map<char*, double> >::iterator sit = samplepropertymap.begin();sit!=samplepropertymap.end();sit++){
		int sid = sit->first;
		std::map<char*, double> pvmap = sit->second;
		int i=0;
		for(std::vector<char*>::iterator fit = features.begin();fit!=features.end();fit++,i++){
			char* f = *fit;
			//commented by ziheng - 2.4.2015
			//I have no idea why pvmap.find(f) doesn't work. Find out the reason later.
			double v = -999.99999;
			//std::map<char*, double>::iterator it = pvmap.find(f);
			for(std::map<char*, double>::iterator it = pvmap.begin();it!=pvmap.end();it++){
				char* key = it->first;
				if(strcmp(f, key)==0){
					v = it->second;		
				}
			}
			if(v==-999.99999){
				std::cout << "Fail to find the key:" << f << "\n";
				throw 300;
			}	
			
			//double v = pvmap[f];
			//double v = it->second;
			//std::cout << "Feature Id:" << f << "- " << v << "\n";
			double newfv = (v-minfv[i])/maxmindis[i];
			pvmap[f] = newfv;
			//std::cout<<"  New normalized sample feature: "<<newfv<<"\n";
		}
		samplepropertymap[sid] = pvmap; //update the sample property map
	}
	std::cout << "  Normalization is over.\n=================\n";

	///////////////////////////////////stop here - 6.16.2.3.2015

	//divide the to-be-classified features and sample features
	
	std::map<int, std::vector<double>* > featuremap = allfeaturemap;
	std::map<int, std::vector<double>* > samplefeaturemap; // samplepropertymap
	//add by Ziheng - 2015.2.3
	for(std::map<int, std::map<char*,double> >::iterator sit = samplepropertymap.begin();sit!=samplepropertymap.end();sit++){
		int sampleid = sit->first;
		std::map<char*, double> kvmap = sit->second;
		std::vector<double>* newkvector = new std::vector<double>;
		int i=0;
		for(std::vector<char*>::iterator fit = features.begin();fit!=features.end();fit++,i++){
			char* f = *fit;
			double v = kvmap[f];
			newkvector->push_back(v);
		}
		samplefeaturemap[sampleid] = newkvector;
	}
	std::cout << "Sample feature map is set. \n";
	/*for(std::map<int,std::vector<double>*>::iterator ait = allfeaturemap.begin();ait!=allfeaturemap.end();ait++){
		int fid = ait->first;
		std::vector<double>* fvalues = ait->second;
		if(isSample(fid,samplemap)){
			samplefeaturemap[fid] = fvalues;
		}else{
			featuremap[fid] = fvalues;
		}	
	}*/
	
	//get the k nearest neighbour of each feature
	for(std::map<int, std::vector<double>* >::iterator kit = featuremap.begin();kit!=featuremap.end();kit++){
		int kid = kit->first;
		std::vector<double>* fs = kit->second;
		//f2dis is used to save the found k nearest samples
		std::map<int,double> f2dis; 
		std::map<int,double> overf2dis;
		for(std::map<int,std::vector<double>* >::iterator sait = samplefeaturemap.begin();sait!=samplefeaturemap.end();sait++){
			int sid = sait->first;
			std::vector<double>* ss = sait->second;
			double dd = distance(fs,ss);
			overf2dis[sid]=dd;
			
			if(f2dis.size()<k){
				f2dis[sid] = dd; 
			}else{
				//find the farthest sample
				int thefarid = -1;
				double thefard = -1;
				for(std::map<int,double>::iterator fait = f2dis.begin();fait!=f2dis.end();fait++){
					int faid = fait->first;
					double fadis = fait->second;
					if(thefard==-1||thefard<fadis){
						thefarid = faid;
						thefard = fadis;
					}
				}
				//Compare the distance of the farthest sample with the current sample. If the distance of the farthest sample is bigger, replace the farthest sample with the current sample.Else, do nothing.
				if(dd>=thefard){
					continue;
				}else{
					f2dis.erase(thefarid);
					f2dis[sid] = dd;
				}
			}
		}
		//get the mostly shown up class in the k samples
		std::map<char*, std::vector<int> >  frequencymap;
		for(std::map<int,double>::iterator f2disit = f2dis.begin();f2disit!=f2dis.end();f2disit++){
			int fcid = f2disit->first;
			//get sample feature's class
			char* cla = samplemap[fcid];
			//check if the class exists in the frequencymap
			std::map<char*, std::vector<int> >::iterator frit = frequencymap.find(cla);
			if(frit==frequencymap.end()){
				std::vector<int> fflist;
				fflist.push_back(fcid);
				frequencymap[cla] = fflist;
			}else{
				std::vector<int> fflist = frequencymap[cla];
				fflist.push_back(fcid);
				frequencymap[cla] = fflist;
			}
		}
		int showupnum = 0;
		char* theclass;
		double theshortestdis = -1.0;
		for(std::map<char*, std::vector<int> >::iterator freit = frequencymap.begin();freit!=frequencymap.end();freit++){
			char* cla = freit->first;
			std::vector<int> fflist = freit->second;
			if(fflist.size()>showupnum){
				theclass = cla;
				showupnum = fflist.size();
				for(std::vector<int>::iterator shit = fflist.begin();shit!=fflist.end();shit++){
                                        int shid = *shit;
                                        double shdis = overf2dis[shid];
                                        if(theshortestdis==-1.0||theshortestdis>shdis){
                                                theshortestdis = shdis;
                                        }
                                }	
			}else if(fflist.size()==showupnum){
				//if equal, judge which class has the shortest distance from the to-be-classified feature. 
				double stst1 = 0.0;
				for(std::vector<int>::iterator shit = fflist.begin();shit!=fflist.end();shit++){
					int shid = *shit;
					double shdis = overf2dis[shid];
					if(stst1==0.0||stst1>shdis){
						stst1 = shdis;
					}		
				}
                                
				if(stst1<theshortestdis){
					theclass = cla;
					theshortestdis = stst1;
				}
			}
		}		
		//Judge whether unclassified features are allowed in the result vector
		if(strcmp(noclass,"true")==0){
			//std::cout<< "The shortest distance is: "<< theshortestdis<<"::Threshold is:"<<thres<<"\n";
			if(theshortestdis<=(1-thres)){
				classmap[kid]=theclass;
			}else{
				classmap[kid]="unclassified";
			}
		}else{
			classmap[kid]=theclass;	
		}
	
	}
	//write the feature properties into the output vector file
        // get vector driver
        OGRSFDriver *poDriver;
        poDriver = OGRSFDriverRegistrar::GetRegistrar()->GetDriverByName( "ESRI Shapefile" );
        if (poDriver == NULL)
        {
                std::cout << "ERR: Fail to get the driver of ESRI Shapefile.\n";
                return 0;
        }

	//output the features with class property
	OGRDataSource* poDstDS=poDriver->CreateDataSource(outputfilepath);
        //Copy the input vector's features into the output vector file
        OGRLayer* poLayer = poDstDS->CopyLayer(layer,"Output",NULL);
        if (poDstDS == NULL)
        {
                std::cout << "ERR : Fail to copy the output vector.";
                OGRDataSource::DestroyDataSource(poDstDS);
                return 0;
        }
	std::cout << "Create feature value fields 'class' \n";
        OGRFieldDefn oField( "class", OFTString );
        if( poLayer->CreateField( &oField ) != OGRERR_NONE )
        {
                std::cout<< "ERR: Creating field 'class' failed.\n";
                throw 501;
        }
        std::cout << "Set the values.\n";
	
	poLayer->ResetReading();
	OGRFeature* f;
	while((f=poLayer->GetNextFeature())!=NULL){
		int field = (int)f->GetFieldAsDouble(idindex);
		char* cl = classmap[field];
		f->SetField("class",cl);
		poLayer->SetFeature(f);
	}
	std::cout << "Values are set.";
	std::cout << "Done,\nRelease the resources.\n";
        //release the resources
        OGRDataSource::DestroyDataSource(poVDS);
        OGRDataSource::DestroyDataSource(poDstDS);

	return 0;
}

int main(int argc,char* argv[]){
        try{
                char* vectorfilepath = "";
                char* outputfilepath = "";
                char* featurelist = "";
		char* k = "";
		char* hierarchy = "";
		char* samples = "";
		char* noclass = "false";
		char* thres = "0.8";
		for(int i=0;i<argc;i++){
			if(strcmp(argv[i],"-i")==0){
					vectorfilepath = argv[i+1];
					printf("vector file path: %s\n",vectorfilepath);
			}else if(strcmp(argv[i],"-h")==0){
				hierarchy = argv[i+1];
				printf("class hierarchy: %s\n", hierarchy);
			}else if(strcmp(argv[i],"-s")==0){
				samples = argv[i+1];
				printf("samples: %s\n", samples); 
			}else if(strcmp(argv[i],"-k")==0){
					k = argv[i+1];
					printf("K value: %s\n",k);
			}else if(strcmp(argv[i],"-o")==0){
					outputfilepath = argv[i+1];
					printf("output file path: %s\n",outputfilepath);
			}else if(strcmp(argv[i],"-fl")==0){
					featurelist = argv[i+1];
					printf("feature list: %s\n",featurelist);
			}else if(strcmp(argv[i],"-noclass")==0){
				noclass = argv[i+1];
				printf("noclass: %s\n",noclass);
			}else if(strcmp(argv[i],"-thres")==0){
				thres = argv[i+1];
				printf("threshold: %s\n",thres);
                        }else if(strcmp(argv[i],"--help")==0){
                                std::cout << "Usage: [-i  input vector file path]" << "\n";
                                std::cout << "       [-k  k value]" << "\n";
                                std::cout << "       [-fl  feature list]" << "\n";
                                std::cout << "       [-o   output file path]" << "\n";
				std::cout << "       [-h   class hierarchy]\n";
				std::cout << "       [-s   sample id list file path]\n";
				std::cout << "       [-noclass   If true, there will be unclassified features in the result;If false,there will not. Default is false.]\n";
				std::cout << "       [-thres  A threshold value to judge whether a feature should be classified or not. This parameter only works when -noclass property is true.Default value is 0.8.]\n";
                                std::cout << "       [--help display the instructions of this tool.]\n";
                                return 0;
			}
                }
                if(strlen(vectorfilepath)==0){
                        std::cout << "Vector file path is not found. "<<"\n";
                        throw 101;
                }else if(strlen(k)==0){
                        std::cout << "K value is not found. " << "\n";
                        throw 102;
                }else if(strlen(featurelist)==0){
                        std::cout << "Feature list is not found." << "\n";
                        throw 103;
                }else if(strlen(outputfilepath)==0){
                        std::cout << "Output file path is not found. " << "\n";
                        throw 104;
                }else if(strlen(hierarchy)==0){
			std::cout << "Class hierarchy is not found.\n";
			throw 105;
		}else if(strlen(samples)==0){
			std::cout << "Sampe list is not found.";
			throw 106;
		}else if(strlen(noclass)==0){
			std::cout << "-noclass is not set. Default 'false' is used.";
			
		}else if(strlen(thres)==0){
			std::cout << "-thres is not set. Default '0.8' is used.";
		}
		
                int ret = KNN_SD( vectorfilepath, outputfilepath, featurelist,  k,  hierarchy, samples, noclass, thres);
                return ret;
        }catch(int x){
                std::cout << "Failed. Error: "<<x<<"\n";

        }
        return 0;
	
		
	
}

