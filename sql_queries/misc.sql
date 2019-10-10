

/*SELECT sstore.sfileid,sname,sFactCode
FROM SSTORE 
JOIN smast on sstore.sfileId=smast.sfileid  
where  sFactCode='5201005079373';*/



select * from sstore where SpaFileIdNo=2

--delete from sstore where sFileId=22102;

--select * from sstore where SpaFileIdNo=1;
--delete from sstore where SpaFileIdNo=2;
--select * from sstore ;
--insert into sstore values (22102,54,2,2,0,0,0,0,0,0);

/*insert into sstore 
(sFileId,SpaFileIdNo,sstRemain1,sstRemain2,sstApogr1,sstApogr2,sstWaiting1,sstWaiting2,sstOrdered1,sstOrdered2) 
values (54,2,2,0,0,0,0,0,0,0);*/


