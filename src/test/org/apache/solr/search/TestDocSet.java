begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|OpenBitSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|BitSetIterator
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|TestDocSet
specifier|public
class|class
name|TestDocSet
extends|extends
name|TestCase
block|{
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|loadfactor
name|float
name|loadfactor
decl_stmt|;
DECL|method|getRandomSet
specifier|public
name|OpenBitSet
name|getRandomSet
parameter_list|(
name|int
name|sz
parameter_list|,
name|int
name|bitsToSet
parameter_list|)
block|{
name|OpenBitSet
name|bs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|==
literal|0
condition|)
return|return
name|bs
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|bs
operator|.
name|fastSet
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|getHashDocSet
specifier|public
name|DocSet
name|getHashDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|bs
operator|.
name|cardinality
argument_list|()
index|]
decl_stmt|;
name|BitSetIterator
name|iter
init|=
operator|new
name|BitSetIterator
argument_list|(
name|bs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getBitDocSet
specifier|public
name|DocSet
name|getBitDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
return|return
operator|new
name|BitDocSet
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|getDocSet
specifier|public
name|DocSet
name|getDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
return|return
name|rand
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|==
literal|0
condition|?
name|getHashDocSet
argument_list|(
name|bs
argument_list|)
else|:
name|getBitDocSet
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|checkEqual
specifier|public
name|void
name|checkEqual
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|,
name|DocSet
name|set
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bs
operator|.
name|capacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|set
operator|.
name|exists
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSingle
specifier|protected
name|void
name|doSingle
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|int
name|sz
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|sz2
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|OpenBitSet
name|a1
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|OpenBitSet
name|a2
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz2
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DocSet
name|b1
init|=
name|getDocSet
argument_list|(
name|a1
argument_list|)
decl_stmt|;
name|DocSet
name|b2
init|=
name|getDocSet
argument_list|(
name|a2
argument_list|)
decl_stmt|;
comment|// System.out.println("b1="+b1+", b2="+b2);
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|a1
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|a2
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_and
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_or
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|a2
argument_list|)
expr_stmt|;
comment|// OpenBitSet a_xor = (OpenBitSet)a1.clone(); a_xor.xor(a2);
name|OpenBitSet
name|a_andn
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_and
argument_list|,
name|b1
operator|.
name|intersection
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_or
argument_list|,
name|b1
operator|.
name|union
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_andn
argument_list|,
name|b1
operator|.
name|andNot
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|intersectionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|unionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|andNotSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doMany
specifier|public
name|void
name|doMany
parameter_list|(
name|int
name|maxSz
parameter_list|,
name|int
name|iter
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|doSingle
argument_list|(
name|maxSz
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomDocSets
specifier|public
name|void
name|testRandomDocSets
parameter_list|()
block|{
name|doMany
argument_list|(
literal|300
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomHashDocset
specifier|public
name|HashDocSet
name|getRandomHashDocset
parameter_list|(
name|int
name|maxSetSize
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|int
name|n
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSetSize
argument_list|)
decl_stmt|;
name|OpenBitSet
name|obs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|n
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|idx
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|obs
operator|.
name|getAndSet
argument_list|(
name|idx
argument_list|)
condition|)
continue|continue;
name|a
index|[
name|i
index|]
operator|=
name|idx
expr_stmt|;
break|break;
block|}
block|}
return|return
name|loadfactor
operator|!=
literal|0
condition|?
operator|new
name|HashDocSet
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|n
argument_list|,
literal|1
operator|/
name|loadfactor
argument_list|)
else|:
operator|new
name|HashDocSet
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
DECL|method|getRandomHashSets
specifier|public
name|DocSet
index|[]
name|getRandomHashSets
parameter_list|(
name|int
name|nSets
parameter_list|,
name|int
name|maxSetSize
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|DocSet
index|[]
name|sets
init|=
operator|new
name|DocSet
index|[
name|nSets
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nSets
condition|;
name|i
operator|++
control|)
block|{
name|sets
index|[
name|i
index|]
operator|=
name|getRandomHashDocset
argument_list|(
name|maxSetSize
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
comment|/**** needs code insertion into HashDocSet   public void testCollisions() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=256;     int iter=1;     int[] maxDocs=new int[] {100000,500000,1000000,5000000,10000000};     int ret=0;     long start=System.currentTimeMillis();     for (int maxDoc : maxDocs) {       int cstart = HashDocSet.collisions;       DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);       for (DocSet s1 : sets) {         for (DocSet s2 : sets) {           if (s1!=s2) ret += s1.intersectionSize(s2);         }       }       int cend = HashDocSet.collisions;       System.out.println("maxDoc="+maxDoc+"\tcollisions="+(cend-cstart));           }     long end=System.currentTimeMillis();     System.out.println("testIntersectionSizePerformance="+(end-start)+" ms");     if (ret==-1)System.out.println("wow!");     System.out.println("collisions="+HashDocSet.collisions);    }   ***/
comment|/***   public void testIntersectionSizePerformance() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=128;     int iter=10;     int maxDoc=1000000;     DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);     int ret=0;     long start=System.currentTimeMillis();     for (int i=0; i<iter; i++) {       for (DocSet s1 : sets) {         for (DocSet s2 : sets) {           ret += s1.intersectionSize(s2);         }       }     }     long end=System.currentTimeMillis();     System.out.println("testIntersectionSizePerformance="+(end-start)+" ms");     if (ret==-1)System.out.println("wow!");   }     public void testExistsPerformance() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=512;     int iter=1;     int maxDoc=1000000;     DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);     int ret=0;     long start=System.currentTimeMillis();     for (int i=0; i<iter; i++) {       for (DocSet s1 : sets) {         for (int j=0; j<maxDoc; j++) {           ret += s1.exists(j) ? 1 :0;         }       }     }     long end=System.currentTimeMillis();     System.out.println("testExistsSizePerformance="+(end-start)+" ms");     if (ret==-1)System.out.println("wow!");   }    ***/
comment|/**** needs code insertion into HashDocSet    public void testExistsCollisions() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=512;     int[] maxDocs=new int[] {100000,500000,1000000,5000000,10000000};     int ret=0;      for (int maxDoc : maxDocs) {       int mask = (BitUtil.nextHighestPowerOfTwo(maxDoc)>>1)-1;       DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);       int cstart = HashDocSet.collisions;             for (DocSet s1 : sets) {         for (int j=0; j<maxDocs[0]; j++) {           int idx = rand.nextInt()&mask;           ret += s1.exists(idx) ? 1 :0;         }       }       int cend = HashDocSet.collisions;       System.out.println("maxDoc="+maxDoc+"\tcollisions="+(cend-cstart));     }     if (ret==-1)System.out.println("wow!");     System.out.println("collisions="+HashDocSet.collisions);   }   ***/
block|}
end_class
end_unit
