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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Name of the index deletable file */
DECL|field|DELETABLE
specifier|static
specifier|final
name|String
name|DELETABLE
init|=
literal|"deletable"
decl_stmt|;
comment|/**    * This array contains all filename extensions used by Lucene's index files, with    * one exception, namely the extension made up from<code>.f</code> + a number.    * Also note that two of Lucene's files (<code>deletable</code> and    *<code>segments</code>) don't have any filename extension.    */
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
literal|"tvp"
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
block|}
end_class
end_unit
