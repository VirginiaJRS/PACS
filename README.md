PACS online system
---------------------------------------------------------
parameterless automatic classification system

The source code of PACS aims to help you understand the backstage mechanism of processing an image in parameterless and automatic style. 

It include three parts: web site, web service and workflow. 

The important module in the website part is the code under the src/ folder which is responsible for reasoning an appropriate classification parameter configuration map for a specific image based a ontology database. The ontologies are stored in a MySQL database.

The web serivce part is responsible for actually processing images. It include a series of steps such as unsupervised classification, eliminating small objects, combining bands into one, turning raster to vector and supervised classification based on a sample database and the automatically generated input parameter configuration. The source code is written in C++. 

The workflow part includes a workflow instance which is a jar. It can be zipped into two documents: a WSDL file and a BPEL file. The WSDL file describes the input parameters the workflow requires. The values for those input parameters are automatically reasoned out by the servlet program in the website part. The BPEL records the logic sequence of all the calls to the webservices to operate on the target image. The final output of the BPEL instance is a classified vector file. The workflow can be automatically executed by any BPEL engine. 

If you use the code, pleae cite our paper published on Computers & Geosciences.

Sun, Z., Fang, H., Di, L., & Yue, P. (2016). Realizing parameterless automatic classification of remote sensing imagery using ontology engineering and cyberinfrastructure techniques. Computers & Geosciences, 94, 56-67.
