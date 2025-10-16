package com.ikonicit.resource.tracker.service;



import com.ikonicit.resource.tracker.entity.Internship;
import com.ikonicit.resource.tracker.repository.InternshipRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.awt.Color.BLACK;


@Service
public class OpenPDFServiceImpl implements OpenPDFService{

    @Autowired
    private InternshipRepository internshipRepository;

    public byte[] generatePdf(Integer id) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            document.open();
            addContent(document, id);

            if (writer.getPageNumber() > 0) {
                document.close();
                writer.flush();
                return baos.toByteArray();
            } else {
                System.err.println("The document has no pages. No PDF will be generated.");
                return null;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void addContent(Document document,Integer id) {
        try {
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BLACK);
            Font boldFonts = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLACK);


            final var logoPath = new ClassPathResource("images/newlogo.jpg").getInputStream();
            float scalePercentage = 50;
            float targetWidth = 200f;
            float targetHeight = 100f;

            final var logo = Image.getInstance(IOUtils.toByteArray(logoPath));
            logo.scalePercent(scalePercentage);
            logo.scaleAbsolute(targetWidth, targetHeight);

            document.add(logo);
            addEmptyLine(document, 2);
            Paragraph title = new Paragraph("Internship Offer Letter", boldFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            addEmptyLine(document, 2);



            document.add(new Paragraph("PLOT NO 45,46\nCENTRAL PARK, PHASE-1, KONDAPUR\nRANGAREDDY, TELANGANA - 500084"));
            document.add(new Paragraph("Contact: +91 81437 54111"));
            document.add(new Paragraph("E-Mail: hr@sscreativelabs.com"));
            Internship internship = internshipRepository.findById(id).orElse(null);




            addEmptyLine(document, 2);

//            document.add(new Paragraph(internship.getOfferLetterReleasingDate()));

            addEmptyLine(document, 1);

            document.add(new Paragraph(internship.getName()));

            document.add(new Paragraph(internship.getTown()));
            document.add(new Paragraph(internship.getDistrict()));
            document.add(new Paragraph("Contact: +91"+" "+internship.getPhone()));
            document.add(new Paragraph("E-Mail:" +" "+internship.getEmail()));




            addEmptyLine(document, 1);

            document.add(new Paragraph("Dear" + " " + internship.getName(),boldFonts));

            // Add space
            addEmptyLine(document, 1);

            StringBuilder text = new StringBuilder();

            // Append the text without new paragraphs
            text.append("We are delighted to offer you an internship position as a ")
                    .append(getById(id).getInternPosition())
                    .append(" at Sunshine Creative Labs, commencing on ")
                    .append(getById(id).getCommencmentDate())
                    .append(". We believe your skills and enthusiasm will be valuable additions to our team.");

            // Create a single Paragraph with the concatenated text
            Paragraph paragraph = new Paragraph(text.toString());

            // Add the Paragraph to the Document
            document.add(paragraph);
            addEmptyLine(document, 1);

            document.add(new Paragraph("Details of the Internship:",boldFonts));
            addEmptyLine(document, 1);
            document.add(new Paragraph("Position:" + " " + internship.getInternPosition()));
            document.add(new Paragraph("Location: Hyderabad (Remote)"));
            document.add(new Paragraph("Compensation: No Stipend,"));
            document.add(new Paragraph("Reporting to: [Supervisor/Manager Name]"));
            addEmptyLine(document, 1);
            addEmptyLine(document, 1);

            addEmptyLine(document, 1);
            addEmptyLine(document, 3);


            document.add(new Paragraph("Responsibilities:",boldFonts));
            addEmptyLine(document, 1);
            if ("Frontend Developer".equals(internship.getInternPosition())) {
                StringBuilder text3 = new StringBuilder();

                // Append the text without new paragraphs for the third block
                text3.append("As a Frontend Development Intern, you'll collaborate on creating visually appealing and responsive web interfaces. ")
                        .append("Responsibilities include implementing UI designs, structuring HTML markup and styling with CSS, ")
                        .append("and utilizing JavaScript and compatibility frameworks for client-side to interact. ")
                        .append("Gain hands-on experience in cross-browser responsive design, API integration, ")
                        .append("and contribute to a dynamic development environment.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph3 = new Paragraph(text3.toString());

                // Add the Paragraph to the Document
                document.add(paragraph3);

            } else if ("AWS DevOps Engineer".equals(internship.getInternPosition())) {
                StringBuilder text4 = new StringBuilder();

                // Append the text without new paragraphs for the fourth block
                text4.append("Streamline processes and improve system reliability. Assist in designing, implementing, and maintaining automation tools. ")
                        .append("Support the team in deploying, managing, and monitoring cloud-based applications. ")
                        .append("Participate in troubleshooting and resolving infrastructure issues.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph4 = new Paragraph(text4.toString());

                // Add the Paragraph to the Document
                document.add(paragraph4);
            } else if ("Software Tester".equals(internship.getInternPosition())) {

                StringBuilder text5 = new StringBuilder();

                // Append the text without new paragraphs for the fifth block
                text5.append("Tester plays a crucial role in the software development life cycle by ensuring that software applications are of high quality, ")
                        .append("reliable, and free of defects, meeting client expectations. ")
                        .append("Tester responsibilities may vary based on the development methodology and the specific requirements of projects. ")
                        .append("Collaborating closely with the software development team and the client.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph5 = new Paragraph(text5.toString());

                // Add the Paragraph to the Document
                document.add(paragraph5);
            } else if ("Java Developer".equals(internship.getInternPosition())) {
                StringBuilder text6 = new StringBuilder();

                // Append the text without new paragraphs for the sixth block
                text6.append("As a Backend Java Developer, you'll contribute to building robust and efficient server-side applications. ")
                        .append("Responsibilities include designing and implementing scalable backend solutions, optimizing database performance, ")
                        .append("and ensuring secure data management. Work with Java, Spring, and database technologies to support the development ")
                        .append("of server-side logic. Gain hands-on experience in handling data storage, API development, and collaborating in a dynamic ")
                        .append("development environment.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph6 = new Paragraph(text6.toString());

                // Add the Paragraph to the Document
                document.add(paragraph6);
            } else if ("Software Engineer".equals(internship.getInternPosition())) {
                StringBuilder text1 = new StringBuilder();

                // Append the text without new paragraphs
                text1.append("Collaborating closely with the Software Engineer team, I focus on optimizing processes and ")
                        .append("enhancing system reliability. My responsibilities include designing, implementing, maintaining ")
                        .append("tools to streamline workflows within the environment. Participate in the troubleshooting and ")
                        .append("resolving infrastructure issues. At the conclusion of the internship, the decision to ")
                        .append("transition into a regular position will be based on your performance evaluation. We are ")
                        .append("excited about the prospect of you becoming an integral part of our team.");

                Paragraph paragraph1 = new Paragraph(text1.toString());

                document.add(paragraph1);
            }
            else if ("ReactJS Developer".equals(internship.getInternPosition())){
                StringBuilder text7 = new StringBuilder();

                // Append the text without new paragraphs for the seventh block
                text7.append("Developing user interface components using React.js concepts and workflows such as Redux, Flux, and Context API. ")
                        .append("Building reusable components and front-end libraries for the future. ")
                        .append("Conducting code reviews and ensuring adherence to best practices and coding standards. ")
                        .append("Working closely with back-end developers to integrate front-end and back-end components. ")
                        .append("Troubleshooting and debugging issues to ensure smooth functionality. ")
                        .append("Staying updated on emerging technologies and industry trends to continuously improve development processes. ")
                        .append("Collaborating closely with the Software Engineer team, focusing on optimizing processes.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph7 = new Paragraph(text7.toString());

                // Add the Paragraph to the Document
                document.add(paragraph7);;
            } else if ("Angular Developer".equals(internship.getInternPosition())) {
                StringBuilder text9 = new StringBuilder();

                // Append the text without new paragraphs for the ninth block
                text9.append("Writing clean, maintainable, and efficient code using Angular framework to develop front-end web applications. ")
                        .append("Creating reusable components and modules that can be easily integrated across multiple pages or projects, ")
                        .append("promoting code reusability and maintainability. Writing unit tests and end-to-end tests to ensure the reliability ")
                        .append("and stability of the application. Debugging and resolving issues as they arise during development or testing phases.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph9 = new Paragraph(text9.toString());

                // Add the Paragraph to the Document
                document.add(paragraph9);
                
            } else {
                StringBuilder text8 = new StringBuilder();

                // Append the text without new paragraphs for the eighth block
                text8.append("You'll contribute to building robust and efficient server-side applications. ")
                        .append("Responsibilities include designing scalable backend solutions, optimizing database performance, ")
                        .append("and ensuring secure data management. Work with Java, frameworks, and database technologies to support the ")
                        .append("development of server-side logic. Gain hands-on experience in handling data storage, API development, ")
                        .append("and collaborating in a dynamic development environment.");

                // Create a single Paragraph with the concatenated text
                Paragraph paragraph8 = new Paragraph(text8.toString());

                // Add the Paragraph to the Document
                document.add(paragraph8);            }

            // Add space
            addEmptyLine(document, 1);
            StringBuilder text2 = new StringBuilder();

            text2.append("Please indicate your acceptance of this internship offer by signing and returning a copy of this ")
                    .append("letter by ").append(" ").append(internship.getSubmissionDate()).append(".")
                    .append(" We look forward to welcoming you to Sunshine Creative Labs and working ")
                    .append("together towards shared success.");

            // Create a single Paragraph with the concatenated text
            Paragraph paragraph2 = new Paragraph(text2.toString());
            document.add(paragraph2);

            addEmptyLine(document, 2);

            document.add(new Paragraph("Sincerely,",boldFonts));
            addEmptyLine(document, 2);


            document.add(new Paragraph("Sunshine Creative Labs",boldFonts));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEmptyLine(Document document, int lines) throws Exception {
        for (int i = 0; i < lines; i++) {
            document.add(new Paragraph(" "));
        }
    }



    public Internship create(Internship internship) {

        return  internshipRepository.save(internship);
    }

    public Internship getById(Integer id) {
        return internshipRepository.findById(id).orElse(null);
    }

    @Override
    public String delete(Integer id) {
        internshipRepository.deleteById(id);
        return "deleted";
    }



    public Internship update(Internship updatedInternship,Integer id) {
        Optional<Internship> existingInternshipOptional = internshipRepository.findById(updatedInternship.getId());

        if (existingInternshipOptional.isPresent()) {
            Internship existingInternship = existingInternshipOptional.get();

            existingInternship.setName(updatedInternship.getName());
            existingInternship.setOfferLetterReleasingDate(updatedInternship.getOfferLetterReleasingDate());
            existingInternship.setTown(updatedInternship.getTown());
            existingInternship.setDistrict(updatedInternship.getDistrict());
            existingInternship.setInternPosition(updatedInternship.getInternPosition());
            existingInternship.setCommencmentDate(updatedInternship.getCommencmentDate());
            existingInternship.setSubmissionDate(updatedInternship.getSubmissionDate());
            existingInternship.setEmail(updatedInternship.getEmail());
            existingInternship.setPhone(updatedInternship.getPhone());


            return internshipRepository.save(existingInternship);
        } else {

            return null;
        }
    }

    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }

}
