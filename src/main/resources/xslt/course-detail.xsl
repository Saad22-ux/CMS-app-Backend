<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="courseId"/>

    <xsl:template match="/">
        <html>
            <body>
                <xsl:for-each select="courses/course[@id=$courseId]">
                    <h1><xsl:value-of select="title"/></h1>
                    <p><b>Auteur:</b> <xsl:value-of select="author"/></p>
                    <p><b>Catégorie:</b> <xsl:value-of select="@category"/></p>
                    <p><b>Description:</b> <xsl:value-of select="description"/></p>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
