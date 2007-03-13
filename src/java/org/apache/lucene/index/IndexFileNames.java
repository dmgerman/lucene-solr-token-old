begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Useful constants representing filenames and extensions used by lucene  *   * @author Bernhard Messer  * @version $rcs = ' $Id: Exp $ ' ;  */
end_comment
begin_class
DECL|class|IndexFileNames
specifier|final
class|class
name|IndexFileNames
block|{
comment|/** Name of the index segment file */
DECL|field|SEGMENTS
specifier|static
specifier|final
name|String
name|SEGMENTS
init|=
literal|"segments"
decl_stmt|;
comment|/** Name of the generation reference file name */
DECL|field|SEGMENTS_GEN
specifier|static
specifier|final
name|String
name|SEGMENTS_GEN
init|=
literal|"segments.gen"
decl_stmt|;
comment|/** Name of the index deletable file (only used in    * pre-lockless indices) */
DECL|field|DELETABLE
specifier|static
specifier|final
name|String
name|DELETABLE
init|=
literal|"deletable"
decl_stmt|;
comment|/** Extension of norms file */
DECL|field|NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|NORMS_EXTENSION
init|=
literal|"nrm"
decl_stmt|;
comment|/** Extension of compound file */
DECL|field|COMPOUND_FILE_EXTENSION
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_EXTENSION
init|=
literal|"cfs"
decl_stmt|;
comment|/** Extension of deletes */
DECL|field|DELETES_EXTENSION
specifier|static
specifier|final
name|String
name|DELETES_EXTENSION
init|=
literal|"del"
decl_stmt|;
comment|/** Extension of single norms */
DECL|field|SINGLE_NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|SINGLE_NORMS_EXTENSION
init|=
literal|"f"
decl_stmt|;
comment|/** Extension of separate norms */
DECL|field|SEPARATE_NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|SEPARATE_NORMS_EXTENSION
init|=
literal|"s"
decl_stmt|;
comment|/**    * This array contains all filename extensions used by    * Lucene's index files, with two exceptions, namely the    * extension made up from<code>.f</code> + a number and    * from<code>.s</code> + a number.  Also note that    * Lucene's<code>segments_N</code> files do not have any    * filename extension.    */
DECL|field|INDEX_EXTENSIONS
specifier|static
specifier|final
name|String
name|INDEX_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"cfs"
block|,
literal|"fnm"
block|,
literal|"fdx"
block|,
literal|"fdt"
block|,
literal|"tii"
block|,
literal|"tis"
block|,
literal|"frq"
block|,
literal|"prx"
block|,
literal|"del"
block|,
literal|"tvx"
block|,
literal|"tvd"
block|,
literal|"tvf"
block|,
literal|"gen"
block|,
literal|"nrm"
block|}
decl_stmt|;
comment|/** File extensions that are added to a compound file    * (same as above, minus "del", "gen", "cfs"). */
DECL|field|INDEX_EXTENSIONS_IN_COMPOUND_FILE
specifier|static
specifier|final
name|String
index|[]
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
init|=
operator|new
name|String
index|[]
block|{
literal|"fnm"
block|,
literal|"fdx"
block|,
literal|"fdt"
block|,
literal|"tii"
block|,
literal|"tis"
block|,
literal|"frq"
block|,
literal|"prx"
block|,
literal|"tvx"
block|,
literal|"tvd"
block|,
literal|"tvf"
block|,
literal|"nrm"
block|}
decl_stmt|;
comment|/** File extensions of old-style index files */
DECL|field|COMPOUND_EXTENSIONS
specifier|static
specifier|final
name|String
name|COMPOUND_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"fnm"
block|,
literal|"frq"
block|,
literal|"prx"
block|,
literal|"fdx"
block|,
literal|"fdt"
block|,
literal|"tii"
block|,
literal|"tis"
block|}
decl_stmt|;
comment|/** File extensions for term vector support */
DECL|field|VECTOR_EXTENSIONS
specifier|static
specifier|final
name|String
name|VECTOR_EXTENSIONS
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"tvx"
block|,
literal|"tvd"
block|,
literal|"tvf"
block|}
decl_stmt|;
comment|/**    * Computes the full file name from base, extension and    * generation.  If the generation is -1, the file name is    * null.  If it's 0, the file name is<base><extension>.    * If it's> 0, the file name is<base>_<generation><extension>.    *    * @param base -- main part of the file name    * @param extension -- extension of the filename (including .)    * @param gen -- generation    */
DECL|method|fileNameFromGeneration
specifier|static
specifier|final
name|String
name|fileNameFromGeneration
parameter_list|(
name|String
name|base
parameter_list|,
name|String
name|extension
parameter_list|,
name|long
name|gen
parameter_list|)
block|{
if|if
condition|(
name|gen
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|gen
operator|==
literal|0
condition|)
block|{
return|return
name|base
operator|+
name|extension
return|;
block|}
else|else
block|{
return|return
name|base
operator|+
literal|"_"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|gen
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
operator|+
name|extension
return|;
block|}
block|}
block|}
end_class
end_unit
