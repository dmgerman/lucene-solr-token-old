begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
operator|.
name|DistanceUtils
import|;
end_import
begin_class
DECL|class|DistanceCheck
specifier|public
class|class
name|DistanceCheck
block|{
comment|/**    * @param args    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|double
name|lat1
init|=
literal|0
decl_stmt|;
name|double
name|long1
init|=
literal|0
decl_stmt|;
name|double
name|lat2
init|=
literal|0
decl_stmt|;
name|double
name|long2
init|=
literal|0
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
literal|90
condition|;
name|i
operator|++
control|)
block|{
name|double
name|dis
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|lat1
argument_list|,
name|long1
argument_list|,
name|lat2
argument_list|,
name|long2
argument_list|)
decl_stmt|;
name|lat1
operator|+=
literal|1
expr_stmt|;
name|lat2
operator|=
name|lat1
operator|+
literal|0.001
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|lat1
operator|+
literal|","
operator|+
name|long1
operator|+
literal|","
operator|+
name|lat2
operator|+
literal|","
operator|+
name|long2
operator|+
literal|","
operator|+
name|formatDistance
argument_list|(
name|dis
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|formatDistance
specifier|public
specifier|static
name|String
name|formatDistance
parameter_list|(
name|Double
name|d
parameter_list|)
block|{
name|DecimalFormat
name|df1
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"####.000000"
argument_list|)
decl_stmt|;
return|return
name|df1
operator|.
name|format
argument_list|(
name|d
argument_list|)
return|;
block|}
block|}
end_class
end_unit
