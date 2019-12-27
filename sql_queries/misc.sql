

/*SELECT sstore.sfileid,sname,sFactCode
FROM SSTORE 
JOIN smast on sstore.sfileId=smast.sfileid  
where  sFactCode='5201005079373';*/

-- << Requested from Antoni : prints PDA transactions for a specific document(parastatiko). >>
   /* SELECT stdate,stDoc,sfactCode,sName,stquant,stTranskind,stLocation,stComment
    FROM STRN JOIN SMAST  on SMAST.sfileid=strn.sfileid and stDoc='4'*/

-- << Prints the the average cost price for bread. >>
    --SELECT sLastPrcPr FROM SMAST where sFactCode='43.13';

-- << Prints all the details for products in the warehouse.>>
    --select * from sstore where SpaFileIdNo=2

--PRONTO KPEMA 12X250ML , PRONTO KPEMA 12X250ML
/*SELECT sstore.sfileid,sname,sFactCode
FROM SSTORE
JOIN smast on sstore.sfileId=smast.sfileid
where sstore.sfileid='17764'; */


--delete from sstore where sFileId=22102;

--select * from sstore where SpaFileIdNo=1;
--delete from sstore where SpaFileIdNo=2;
--select * from sstore ;
--insert into sstore values (22102,54,2,2,0,0,0,0,0,0);



/*
insert into sstore
(sFileId,SpaFileIdNo,sstRemain1,sstRemain2,sstApogr1,sstApogr2,sstWaiting1,sstWaiting2,sstOrdered1,sstOrdered2) 
values (54,2,2,0,0,0,0,0,0,0);

insert into sstore
(sFileId,SpaFileIdNo,sstRemain1,sstRemain2,sstApogr1,sstApogr2,sstWaiting1,sstWaiting2,sstOrdered1,sstOrdered2)
values (9310,2,2,0,0,0,0,0,0,0);
*/






------------------------------------------


/*insert into STRN
( stDate,sFileId,stTransKind,stDoc,stLocation,stQuant,stQuant2,stPrice,stDiscount,stValue)
Values (  '29-OCT-2019', 22390,	6,	4,	1,	 	4,	0,	0,	0,	0)*/

/*insert into STRN
( stDate,sFileId,stTransKind,stDoc,stLocation,stQuant,stQuant2,stPrice,stDiscount,stValue)
Values (  '29-OCT-2019', 22390,	7,	4,	2,	 	4,	0,	0,	0,	0)*/

--select * from SSTORE where SpaFileIdNo=2

--	29/10/2019	22390	7	4	2	 	4	0	0	0	0


--delete from strn where stdoc='4'
--select * from strn where stdoc='4'

/*insert into sstore
(sFileId,SpaFileIdNo,sstRemain1,sstRemain2,sstApogr1,sstApogr2,sstWaiting1,sstWaiting2,sstOrdered1,sstOrdered2)
values (22390,2,4,0,0,0,0,0,0,0)*/


-----------------------------------------------------

---< orders >----
/*

select sname,sfactCode,sLastPrcPr,SpaFileIdNo as apothiki,sstRemain1
,sPerOutQ1,sPerOutQ2,sPerOutQ3,sPerOutQ4,sPerOutQ5,sPerOutQ6,sPerOutQ7
sPerOutQ8,sPerOutQ9,sPerOutQ10,sPerOutQ11,sPerOutQ12
 FROM BLINE2
JOIN BMAST2 on bline2.pofileid=bmast2.pofileid
JOIN smast on bline2.sfileid=smast.sfileid
JOIN SSTORE on SSTORE.sfileid=SMAST.sfileid
where poDocum='ΦΠP000002';
*/














