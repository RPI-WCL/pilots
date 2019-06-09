#!/bin/bash

if [ "$#" -ne 1 ] ; then
    echo "Usage ./aoacheck_generator.sh [model no (1-3)]"
    exit 1
fi    

model=$1
file=AoaCheck.plt

echo "program AoaCheck; /* Model $model */" > $file
echo "  /* aoa: angle of attack, va: airspeed */" >> $file
echo "  inputs aoa, va (t) using closest(t);" >> $file
echo "  constants" >> $file
echo "    V_CRUISE = 110;"  >> $file 
echo "    NORMAL_L = -0.10 * V_CRUISE;" >> $file 
echo "    NORMAL_H =  0.10 * V_CRUISE;" >> $file
echo "    MPS2KNOT = 1.94384;" >> $file

if [ $model == 1 ] ; then
echo "    G        = 9.81;" >> $file
echo "    L        = 1156.6 * G;" >> $file
echo "    RHO      = 16.2;" >> $file
echo "    S        = 1.225;" >> $file
echo "    CL0      = 0.2279;" >> $file
echo "    V_CRUISE = 110;" >> $file
echo "    NORMAL_L = -0.10 * V_CRUISE;" >> $file
echo "    NORMAL_H =  0.10 * V_CRUISE;" >> $file
echo "    PI       = 3.14159;" >> $file
elif [ $model == 2 ] ; then
echo "    K1       = 0.0694;" >> $file
echo "    K2       = 0.3396;" >> $file
elif [ $model == 3 ] ; then
echo "    K1       = 2.90094;" >> $file
echo "    K2       = 0.00024;" >> $file
echo "    K3       = 0.00108;" >> $file
fi

echo "  outputs" >> $file
echo "    aoa, mode at every 1 sec;" >> $file
echo "  errors" >> $file
echo "      e2: va - MPS2KNOT*" >> $file

if [ $model == 1 ] ; then
echo "          sqrt(2*L/(2*PI*(PI/180)*aoa + CL0)*S*RHO);" >> $file
elif [ $model == 2 ] ; then
echo "          sqrt(2*L/(K1*alpha + K2));" >> $file
elif [ $model == 3 ] ; then
echo "          sqrt(K1/(K2*aoa + K3));" >> $file
fi    
    
echo "  signatures" >> $file
echo "    s0(k): e2 = k, NORMAL_L < k, k < NORMAL_H  \"Normal\";" >> $file
echo "    s1(k): e2 = k, k < NORMAL_L, NORMAL_H < k" >> $file
echo "      \"AoA sensor failure\"" >> $file

if [ $model == 1 ] ; then
echo "      estimate aoa = (1/(2*PI))*((MPS2KNOT^2/va^2)*2*L - CL0);" >> $file
elif [ $model == 2 ] ; then
echo "      estimate aoa = (1/K1)*((MPS2KNOT^2/va^2)*2*L - K2);" >> $file   
elif [ $model == 3 ] ; then
echo "      estimate aoa = (1/K2)*((MPS2KNOT^2/va^2)*K1 - K3);" >> $file       
fi    

echo "end;" >> $file
